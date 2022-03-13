package org.evergreen_ils.ui.offline;
import java.util.logging.Logger;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class CheckoutController {

    @FXML ChoiceBox<CircDuration> durationSelect;
    @FXML ChoiceBox<NonCatType> nonCatSelect;
    @FXML DatePicker dueDateSelect;
    @FXML TextField patronBarcodeInput;
    @FXML TextField itemBarcodeInput;
    @FXML Button checkoutButton;
    @FXML RadioButton itemBarcodeRadio;
    @FXML RadioButton nonCatRadio;
    @FXML TextField nonCatCountInput;
    @FXML TableColumn<Transaction, String> patBarcodeCol;
    @FXML TableColumn<Transaction, String> itemBarcodeCol;
    @FXML TableColumn<Transaction, String> dueDateCol;

    @FXML TableView<Transaction> checkoutsTable;
    ObservableList<Transaction> checkoutsList;

    static final Logger logger = Logger.getLogger("org.evergreen_ils.ui.offline");

    @FXML public void initialize() {

        // This is meant to be handled in the markup via onAction, but
        // it's not firing.  *shrug*
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent evt) { setCanCheckout(evt); }
        };

        durationSelect.setOnAction(handler);
        nonCatSelect.setOnAction(handler);

        // TODO these will come from the server.
        nonCatSelect.getItems().add(new NonCatType(1, "Paperback"));
        nonCatSelect.getItems().add(new NonCatType(2, "Newspaper"));

        patBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("patronBarcode"));
        itemBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("itemBarcode"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        checkoutsList = FXCollections.observableArrayList();
        checkoutsTable.setItems(checkoutsList);
    }

    @FXML void setCanCheckout(ActionEvent event) {
        checkoutButton.setDisable(disableCheckoutButton());

        boolean usingBarcode = itemBarcodeRadio.isSelected();
        nonCatSelect.setDisable(usingBarcode);
        itemBarcodeInput.setDisable(!usingBarcode);
    }

    public boolean stringIsNone(String s) {
        return (s == null || s.equals(""));
    }

    @FXML public void handleKeyPress(KeyEvent event) {

        // onAction for TextField's only runs on Enter.
        // This covers cases where the user enters a value and tabs away
        setCanCheckout(null);

        // Fire checkout on Enter within the item barcode field
        if (event.getCode().toString() == "ENTER") {
            EventTarget target = event.getTarget();

            if (target instanceof TextField) {
                TextField field = (TextField) target;

                if ("itemBarcodeInput".equals(field.getId())) {
                    if (!disableCheckoutButton()) {
                        checkout();
                    }
                }
            }
        }
    }

    public boolean disableCheckoutButton() {

        if (stringIsNone(patronBarcodeInput.getText())) {
            return true;
        }

        if (dueDateSelect.getValue() == null && durationSelect.getValue() == null) {
            return true;
        }

        if (itemBarcodeRadio.isSelected()) {
            if (stringIsNone(itemBarcodeInput.getText())) {
                return true;
            }

            if (dueDateSelect.getValue() == null && durationSelect.getValue() == null) {
                return true;
            }

        } else {

            if (nonCatSelect.getValue() == null) {
                return true;
            }

            String count = nonCatCountInput.getText();

            if (stringIsNone(count) || Integer.parseInt(count) < 0) {
                return true;
            }

            System.out.println("COUNT " + count);
        }

        return false;
    }

    public void checkout() {

        String dueDate;

        if (dueDateSelect.getValue() == null) {
            dueDate = calcDueDate(durationSelect.getValue().toValue());
        } else {
            dueDate = dueDateSelect.getValue().toString();
        }

        Transaction xact = new Transaction();
        xact.setPatronBarcode(patronBarcodeInput.getText());
        xact.setItemBarcode(itemBarcodeInput.getText());
        xact.setDueDate(dueDate);
        xact.setAction("checkout");

        try {
            xact = Data.saveTransaction(xact);

            checkoutsList.addAll(xact);

            Data.xactsList.addAll(xact);
            Data.pendingXactsList.addAll(xact);

        } catch (Exception e) {
            e.printStackTrace();
        }

        itemBarcodeInput.setText("");
        itemBarcodeInput.requestFocus();
    }

    String calcDueDate(String interval)  {

        LocalDateTime dueDate = LocalDateTime.now();

        Period period = Period.parse(interval);

        dueDate = dueDate.plus(period);

        return dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}


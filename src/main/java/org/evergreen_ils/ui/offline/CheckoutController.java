package org.evergreen_ils.ui.offline;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

public class CheckoutController {

    @FXML ChoiceBox<Data.CircDuration> durationSelect;
    @FXML ChoiceBox<Data.NonCatType> nonCatSelect;
    @FXML DatePicker dueDateSelect;
    @FXML TextField patronBarcodeInput;
    @FXML TextField itemBarcodeInput;
    @FXML Button checkoutButton;
    @FXML RadioButton itemBarcodeRadio;
    @FXML RadioButton nonCatRadio;
    @FXML TextField nonCatCountInput;
    @FXML TableColumn<Transaction, String> patBarcodeCol;
    @FXML TableColumn<Transaction, String> itemLabelCol;
    @FXML TableColumn<Transaction, String> dueDateCol;
    @FXML TableColumn<Transaction, String> nonCatCountCol;

    @FXML TableView<Transaction> checkoutsTable;
    ObservableList<Transaction> checkoutsList;

    @FXML public void initialize() {

        // This is meant to be handled in the markup via onAction, but
        // it's not firing.  I'm missing something.
        /*
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent evt) { setCanCheckout(evt); }
        };

        durationSelect.setOnAction(handler);
        nonCatSelect.setOnAction(handler);

        */

        for (Data.NonCatType nct: App.data.nonCatTypes) {
            nonCatSelect.getItems().add(nct);
        }

        patBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("patronBarcode"));
        itemLabelCol.setCellValueFactory(new PropertyValueFactory<>("itemLabel"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        nonCatCountCol.setCellValueFactory(new PropertyValueFactory<>("nonCatCount"));

        checkoutsList = FXCollections.observableArrayList();
        checkoutsTable.setItems(checkoutsList);
    }

    @FXML void setCanCheckout(ActionEvent event) {
        /*
        checkoutButton.setDisable(disableCheckoutButton());
        */

        boolean usingBarcode = itemBarcodeRadio.isSelected();

        nonCatSelect.setDisable(usingBarcode);
        nonCatCountInput.setDisable(usingBarcode);
        itemBarcodeInput.setDisable(!usingBarcode);

        if (usingBarcode) {
            nonCatCountInput.setText("1");
        }
    }

    public boolean stringIsNone(String s) {
        return (s == null || s.equals(""));
    }

    /*
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

            if (getNonCatCountValue() == null) {
                return true;
            }
        }

        return false;
    }

    Integer getNonCatCountValue() {
        String count = nonCatCountInput.getText();

        if (stringIsNone(count)) {
            return null;
        }

        try {
            int c = Integer.parseInt(count);
            if (c < 0) { return null; }
            return c;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void checkout() {

        Transaction xact = new Transaction();
        xact.setAction("checkout");
        xact.setPatronBarcode(patronBarcodeInput.getText());

        if (dueDateSelect.getValue() == null) {
            xact.setDueDate(calcDueDate(durationSelect.getValue().toValue()));
        } else {
            xact.setDueDate(dueDateSelect.getValue().toString());
        }

        NonCatType nct = nonCatSelect.getValue();

        if (nct != null) {
            xact.setNonCatType(nct.getId().toString());
            xact.setNonCatCount(getNonCatCountValue().toString());
        } else {
            xact.setItemBarcode(itemBarcodeInput.getText());
        }

        try {
            xact = App.data.saveTransaction(xact);

            checkoutsList.addAll(xact);

            App.data.xactsList.addAll(xact);
            App.data.pendingXactsList.addAll(xact);

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
    */
}

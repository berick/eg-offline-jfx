package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.time.LocalDate;

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

    @FXML public void initialize() {
        System.out.println("INITIALIZE()");

        // This is meant to be handled in the markup via onAction, but
        // it's not firing.  *shrug*
        EventHandler handler = new EventHandler() {
            public void handle(Event evt) { setCanCheckout(null); }
        };

        durationSelect.setOnAction(handler);
        nonCatSelect.setOnAction(handler);

        // TODO these will come from the server.
        nonCatSelect.getItems().add(new NonCatType(1, "Paperback"));
        nonCatSelect.getItems().add(new NonCatType(2, "Newspaper"));
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
        System.out.println("Due Date: " + dueDateSelect.getValue());
        System.out.println("Patron Barcode: " + patronBarcodeInput.getText());

        itemBarcodeInput.setText("");
        itemBarcodeInput.requestFocus();
    }
}


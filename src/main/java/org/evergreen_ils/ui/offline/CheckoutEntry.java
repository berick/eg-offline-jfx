package org.evergreen_ils.ui.offline;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class CheckoutEntry {

    private StringProperty patronBarcode;
    private StringProperty itemBarcode;
    private StringProperty dueDate;

    public CheckoutEntry(String patronBarcode, String itemBarcode, String dueDate) {
        this.setPatronBarcode(patronBarcode);
        this.setItemBarcode(itemBarcode);
        this.setDueDate(dueDate);
    }

    public void setPatronBarcode(String value) {
        patronBarcodeProperty().set(value);
    }

    public String getPatronBarcode() {
        return patronBarcodeProperty().get();
    }

    public StringProperty patronBarcodeProperty() {
        if (patronBarcode == null) {
            patronBarcode = new SimpleStringProperty(this, "patronBarcode");
        }
        return patronBarcode;
    }

    public void setItemBarcode(String value) {
        itemBarcodeProperty().set(value);
    }

    public String getItemBarcode() {
        return itemBarcodeProperty().get();
    }

    public StringProperty itemBarcodeProperty() {
        if (itemBarcode == null) {
            itemBarcode = new SimpleStringProperty(this, "itemBarcode");
        }
        return itemBarcode;
    }

    public void setDueDate(String value) {
        dueDateProperty().set(value);
    }

    public String getDueDate() {
        return dueDateProperty().get();
    }

    public StringProperty dueDateProperty() {
        if (dueDate == null) {
            dueDate = new SimpleStringProperty(this, "dueDate");
        }
        return dueDate;
    }
}



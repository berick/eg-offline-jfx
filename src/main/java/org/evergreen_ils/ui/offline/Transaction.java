package org.evergreen_ils.ui.offline;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.*;

public class Transaction {

    private StringProperty id;
    private StringProperty patronBarcode;
    private StringProperty itemBarcode;
    private StringProperty dueDate;
    private StringProperty backDate;
    private StringProperty action;
    private StringProperty realTime;
    private StringProperty nonCatType;
    private StringProperty nonCatCount;

    public void setRealTime(String value) {
        realTimeProperty().set(value);
    }

    public String getRealTime() {
        return realTimeProperty().get();
    }

    public StringProperty realTimeProperty() {
        if (realTime == null) {
            realTime = new SimpleStringProperty(this, "realTime");
        }
        return realTime;
    }

    public void setId(String value) {
        idProperty().set(value);
    }

    public String getId() {
        return idProperty().get();
    }

    public StringProperty idProperty() {
        if (id == null) {
            id = new SimpleStringProperty(this, "id");
        }
        return id;
    }


    public void setAction(String value) {
        actionProperty().set(value);
    }

    public String getAction() {
        return actionProperty().get();
    }

    public StringProperty actionProperty() {
        if (action == null) {
            action = new SimpleStringProperty(this, "action");
        }
        return action;
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

    public void setBackDate(String value) {
        backDateProperty().set(value);
    }

    public String getBackDate() {
        return backDateProperty().get();
    }

    public StringProperty backDateProperty() {
        if (backDate == null) {
            backDate = new SimpleStringProperty(this, "backDate");
        }
        return backDate;
    }

    public void setNonCatType(String value) {
        nonCatTypeProperty().set(value);
    }

    public String getNonCatType() {
        return nonCatTypeProperty().get();
    }

    public StringProperty nonCatTypeProperty() {
        if (nonCatType == null) {
            nonCatType = new SimpleStringProperty(this, "nonCatType");
        }
        return nonCatType;
    }

    public void setNonCatCount(String value) {
        nonCatCountProperty().set(value);
    }

    public String getNonCatCount() {
        return nonCatCountProperty().get();
    }

    public StringProperty nonCatCountProperty() {
        if (nonCatCount == null) {
            nonCatCount = new SimpleStringProperty(this, "nonCatCount");
        }
        return nonCatCount;
    }

    String toJson() {

        JSONObject obj = new JSONObject();

        obj.put("action", getAction());
        obj.put("due_date", getDueDate());
        obj.put("backdate", getBackDate());
        obj.put("item_barcode", getItemBarcode());
        obj.put("noncat_type", getNonCatType());
        obj.put("noncat_count", getNonCatCount());
        obj.put("patron_barcode", getPatronBarcode());
        obj.put("realtime", getRealTime());

        return obj.toString();
    }
}



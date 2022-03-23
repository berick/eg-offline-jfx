package org.evergreen_ils.ui.offline;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TransactionsController {

    static final Logger logger = 
        Logger.getLogger(App.class.getPackage().getName());

    @FXML TableView<Transaction> xactsTable;

    @FXML TableColumn<Transaction, String> patronBarcodeCol;
    @FXML TableColumn<Transaction, String> itemLabelCol;
    @FXML TableColumn<Transaction, String> dueDateCol;
    @FXML TableColumn<Transaction, String> backDateCol;
    @FXML TableColumn<Transaction, String> actionCol;
    @FXML TableColumn<Transaction, String> nonCatCountCol;
    @FXML TableColumn<Transaction, String> realTimeCol;
    @FXML TableColumn<Transaction, String> exportTimeCol;
    @FXML CheckBox limitToPending;

    @FXML public void initialize() throws IOException {

        patronBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("patronBarcode"));
        itemLabelCol.setCellValueFactory(new PropertyValueFactory<>("itemLabel"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        backDateCol.setCellValueFactory(new PropertyValueFactory<>("backDate"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        nonCatCountCol.setCellValueFactory(new PropertyValueFactory<>("nonCatCount"));
        realTimeCol.setCellValueFactory(new PropertyValueFactory<>("realTime"));
        exportTimeCol.setCellValueFactory(new PropertyValueFactory<>("exportTime"));

        xactsTable.setItems(App.data.pendingXactsList);
    }

    @FXML void exportTransactions() throws IOException, SQLException {
        App.data.exportPendingXactsToFile();
    }

    @FXML void deletePendingTransactions() {
    }

    @FXML void deleteTransactions() {
    }

    @FXML void toggleLimitToPending(ActionEvent event) {
        if (limitToPending.isSelected()) {
            xactsTable.setItems(App.data.pendingXactsList);
        } else {
            xactsTable.setItems(App.data.xactsList);
        }
    }
}


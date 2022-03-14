package org.evergreen_ils.ui.offline;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.CheckBox;

public class TransactionsController {

    @FXML TableView<Transaction> xactsTable;

    @FXML TableColumn<Transaction, String> patronBarcodeCol;
    @FXML TableColumn<Transaction, String> itemBarcodeCol;
    @FXML TableColumn<Transaction, String> dueDateCol;
    @FXML TableColumn<Transaction, String> backDateCol;
    @FXML TableColumn<Transaction, String> actionCol;
    @FXML TableColumn<Transaction, String> nonCatCountCol;
    @FXML TableColumn<Transaction, String> realTimeCol;
    @FXML TableColumn<Transaction, String> exportTimeCol;
    @FXML CheckBox limitToPending;

    @FXML public void initialize() throws IOException {

        patronBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("patronBarcode"));
        itemBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("itemBarcode"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        backDateCol.setCellValueFactory(new PropertyValueFactory<>("backDate"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        nonCatCountCol.setCellValueFactory(new PropertyValueFactory<>("nonCatCount"));
        realTimeCol.setCellValueFactory(new PropertyValueFactory<>("realTime"));
        exportTimeCol.setCellValueFactory(new PropertyValueFactory<>("exportTime"));

        xactsTable.setItems(Data.pendingXactsList);
    }

    @FXML void exportTransactions() throws IOException, SQLException {
        Data.exportPendingXactsToFile();
    }

    @FXML void deletePendingTransactions() {
    }

    @FXML void deleteTransactions() {
    }

    @FXML void toggleLimitToPending(ActionEvent event) {
        if (limitToPending.isSelected()) {
            xactsTable.setItems(Data.pendingXactsList);
        } else {
            xactsTable.setItems(Data.xactsList);
        }
    }
}


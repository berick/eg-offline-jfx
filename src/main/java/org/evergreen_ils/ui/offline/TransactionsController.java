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

public class TransactionsController {

    @FXML TableView<Transaction> xactsTable;

    @FXML TableColumn<Transaction, String> patronBarcodeCol;
    @FXML TableColumn<Transaction, String> itemBarcodeCol;
    @FXML TableColumn<Transaction, String> dueDateCol;
    @FXML TableColumn<Transaction, String> backDateCol;
    @FXML TableColumn<Transaction, String> actionCol;
    @FXML TableColumn<Transaction, String> nonCatCountCol;
    @FXML TableColumn<Transaction, String> realTimeCol;

    @FXML public void initialize() throws IOException {

        patronBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("patronBarcode"));
        itemBarcodeCol.setCellValueFactory(new PropertyValueFactory<>("itemBarcode"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        backDateCol.setCellValueFactory(new PropertyValueFactory<>("backDate"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        nonCatCountCol.setCellValueFactory(new PropertyValueFactory<>("nonCatCount"));
        realTimeCol.setCellValueFactory(new PropertyValueFactory<>("realTime"));

        xactsTable.setItems(Data.xactsList);
    }

    @FXML void exportTransactions() throws IOException, SQLException {
        Data.saveAllTransactionsToFile();
    }

    @FXML void deleteTransactions() {
    }
}

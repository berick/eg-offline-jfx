package org.evergreen_ils.ui.offline;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class TransactionsController {


    @FXML TableView<Transaction> xactsTable;

    @FXML TableColumn patronBarcodeCol;
    @FXML TableColumn itemBarcodeCol;
    @FXML TableColumn dueDateCol;
    @FXML TableColumn backDateCol;
    @FXML TableColumn actionCol;
    @FXML TableColumn nonCatCountCol;
    @FXML TableColumn realTimeCol;

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

    @FXML void exportTransactions() {
    }

    @FXML void deleteTransactions() {
    }

}

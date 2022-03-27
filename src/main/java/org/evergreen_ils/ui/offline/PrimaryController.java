package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class PrimaryController {

    @FXML TabPane tabs;
    @FXML VBox mainVbox;

    @FXML private void initialize() throws java.sql.SQLException {

    }

    @FXML private void close(ActionEvent event) {
        Platform.exit();
    }
}

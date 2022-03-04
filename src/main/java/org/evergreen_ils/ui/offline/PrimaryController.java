package org.evergreen_ils.ui.offline;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;

public class PrimaryController {

    MenuItem fileMenuClose;

    @FXML private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML private void close(ActionEvent event) {
        Platform.exit();
    }
}

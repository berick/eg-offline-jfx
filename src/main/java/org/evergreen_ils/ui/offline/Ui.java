package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.scene.control.Alert;

//import java.text.MessageFormat;

/**
 * Shared UI controls
 */
public class Ui {

    static void alertAndExit(String msg) {

        App.logger.severe(msg);

        // runLater just in case we're fired from another thread

        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, msg).showAndWait();
            App.shutdown();
        });
    }
}


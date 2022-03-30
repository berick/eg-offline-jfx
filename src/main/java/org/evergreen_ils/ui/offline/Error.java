package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.scene.control.Alert;

//import java.text.MessageFormat;

/**
 * Global error handler.
 */
public class Error {

    /**
     * Alert the user and shutdown.
     *
     * @param e May be null
     * @param msg User-facing error message.
     */
    static void alertAndExit(Throwable e, String msg) {

        final String fullMsg = e == null ? msg : msg + "\n" + e;

        if (e != null) {
            e.printStackTrace();
            App.logger.severe(e.getMessage());
        }

        App.logger.severe(fullMsg);

        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, fullMsg).showAndWait();
            App.shutdown();
        });
    }
}


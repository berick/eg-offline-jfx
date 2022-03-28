package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

//import java.text.MessageFormat;

/**
 * Shared UI controls
 */
public class Ui {

    static void applyOnlineBorder(Region region, boolean isOnline) {

        BorderStroke stroke;

        if (isOnline) {
            // TODO this should be managed in CSS

            stroke = new BorderStroke(
                Color.GREEN, BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, new BorderWidths(5)
            );

        } else {

            stroke = new BorderStroke(
                Color.YELLOW, BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, new BorderWidths(5)
            );
        }

        Border border = new Border(stroke);
        region.setBorder(border);  
    }

    static void alertAndExit(String msg) {

        App.logger.severe(msg);

        new Alert(Alert.AlertType.ERROR, msg).showAndWait();

        Platform.exit();
    }
}


package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.layout.CornerRadii;

import java.util.concurrent.TimeUnit;

import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.ResourceBundle;

public class PrimaryController {

    /**
     * Watches for updates to our online status and applies UI changes
     * to indicate the new state
     */
    class OnlineWatcher implements Runnable {
        public void run() {
            while (!App.shuttingDown) {
                try {
                    applyOnlineBorder(
                        App.data.net.status.queue.poll(10, TimeUnit.SECONDS)
                    );
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    OnlineWatcher onlineWatcher;

    @FXML TabPane tabs;
    @FXML Tab tab1;
    @FXML Tab tab2;
    @FXML VBox mainVbox;
    @FXML VBox bodyVbox;
    @FXML Text locationText; // Apply after setting Config

    @FXML private void initialize() {
        App.primaryController = this;
        setupStrings();
        setStatusLabel(); // clear it

        onlineWatcher = new OnlineWatcher();
        new Thread(onlineWatcher).start();

        setupData();
        setBodyContent("host");
    }

    void setStatusLabel() {
        locationText.setText(App.data.context.toString());
    }

    void setupData() {
        try {
            App.data.setup();
        } catch (Exception e) {
            e.printStackTrace();
            Error.alertAndExit(e, "Could not create offline database");
        }
    }

    @FXML void test() {
        App.progress.startProgressTimer(10);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {}
                App.progress.stopProgressTimer();
            }
        }).start();
    }

    void setupStrings() {
        App.logger.info("Loading string bundle for locale " + Locale.getDefault());

        try {
		    App.strings =
                ResourceBundle.getBundle("org.evergreen_ils.ui.offline.strings");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (App.strings == null) {
            Error.alertAndExit(null,
                "Failed to load string bundle for " + Locale.getDefault());
        }
    }

    /**
     * Sets the content of the main body of the primary controller to the
     * provided fxml file name.
     * @param fxml FXML file name (without '.fxml')
     */
    void setBodyContent(String fxml) {
        bodyVbox.getChildren().clear();
        App.logger.info("Setting body content to: " + fxml);
        bodyVbox.getChildren().add(App.loadFXML(fxml));
    }

    @FXML private void showHost(ActionEvent event) {
        setBodyContent("host");
    }

    @FXML private void close(ActionEvent event) {
        App.shutdown();
    }

    void applyOnlineBorder(Boolean online) {
        if (online == null) { return; }

        App.logger.info("Updating online status display online = " + online);

        BorderStroke stroke;

        if (App.data.net.status.isOnline) {

            // TODO CSS?
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
        Platform.runLater(() -> mainVbox.setBorder(border));
    }
}

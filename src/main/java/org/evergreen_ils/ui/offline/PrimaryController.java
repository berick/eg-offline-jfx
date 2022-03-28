package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;

import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.ResourceBundle;

public class PrimaryController {

    @FXML TabPane tabs;
    @FXML Tab tab1;
    @FXML Tab tab2;
    @FXML VBox bodyVbox;

    @FXML private void initialize() {
        setupStrings();
        setupData();
    }

    void setupData() {
        // connect to db
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
            Ui.alertAndExit(
                "Failed to load string bundle for " + Locale.getDefault());
        }
    }

    @FXML private void showHost(ActionEvent event) {
        bodyVbox.getChildren().add(App.loadFXML("host"));
    }

    @FXML private void close(ActionEvent event) {
        Platform.exit();
    }
    
}

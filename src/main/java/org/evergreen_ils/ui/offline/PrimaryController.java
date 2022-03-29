package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;

import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.ResourceBundle;

public class PrimaryController {

    @FXML TabPane tabs;
    @FXML Tab tab1;
    @FXML Tab tab2;
    @FXML VBox bodyVbox;
    @FXML Text locationText; // Apply after setting Config

    @FXML private void initialize() {
        App.primaryController = this;
        setupStrings();
        setupData();
        setBodyContent("host");
        locationText.setText(App.data.context.toString());
    }

    void setupData() {
        try {
            App.data.setup();
        } catch (Exception e) {
            e.printStackTrace();
            Ui.alertAndExit("Could not create offline database");
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
            Ui.alertAndExit(
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
        Platform.exit();
    }
}

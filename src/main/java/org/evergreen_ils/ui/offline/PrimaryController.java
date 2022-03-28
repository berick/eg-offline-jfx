package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.ResourceBundle;

public class PrimaryController {

    @FXML TabPane tabs;
    @FXML Tab tab1;
    @FXML Tab tab2;
    @FXML VBox bodyVbox;
    @FXML ProgressBar progressBar;

    @FXML private void initialize() {
        setupStrings();
        setupData();
    }

    @FXML private void test() {
        showProgressTimer(10); // TODO
    }

    void setupData() {
        // connect to db
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

    void showProgress() {
        progressBar.setVisible(true);
    }

    void setProgress(float p) {
        progressBar.setProgress(p);
    }

    void hideProgress() {
        progressBar.setVisible(false);
    }

    // TODO return the thread so it can be stopped?
    void showProgressTimer(float seconds) {

        class Timer implements Runnable {
            public void run() {

                float t = 1;
                while (t <= seconds) {
                    try {
                        Thread.sleep(1000);
                    } catch (java.lang.InterruptedException e) {
                        hideProgress();
                        return;
                    }
                    float duration = t / seconds;
                    setProgress(duration);
                    t++;
                }
                hideProgress();
            }
        }

        showProgress();
        new Thread(new Timer()).start();
    }
}

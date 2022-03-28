package org.evergreen_ils.ui.offline;

import javafx.scene.control.ProgressBar;
import javafx.fxml.FXML;

public class ProgressController {

    @FXML ProgressBar progressBar;

    @FXML private void initialize() {
        App.progress = this;
    }
    
    void show() {
        progressBar.setVisible(true);
    }

    void set(float p) {
        progressBar.setProgress(p);
    }

    void hide() {
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
                        hide();
                        return;
                    }
                    float duration = t / seconds;
                    set(duration);
                    t++;
                }
                hide();
            }
        }

        show();
        new Thread(new Timer()).start();
    }
}

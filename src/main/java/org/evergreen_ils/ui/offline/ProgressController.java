package org.evergreen_ils.ui.offline;

import javafx.scene.control.ProgressBar;
import javafx.fxml.FXML;

public class ProgressController {

    @FXML ProgressBar progressBar;

    private static Thread timerThread;

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

    void stopProgressTimer() {
        if (ProgressController.timerThread != null) {
            ProgressController.timerThread.interrupt();
        }
    }

    void startProgressTimer(float seconds) {

        class Timer implements Runnable {
            public void run() {

                float t = 1;
                while (t <= seconds) {
                    try {
                        Thread.sleep(1000);
                    } catch (java.lang.InterruptedException e) {
                        App.logger.info("Progress thread interrupted.  Exiting");
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

        ProgressController.timerThread = new Thread(new Timer());
        ProgressController.timerThread.start();
    }
}

package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;

import java.util.List;

public class HostController {
    @FXML ComboBox<String> hostSelect;

    @FXML private void initialize() {

        List<Context> ctxList;

        try {
            ctxList = App.data.database.loadKnownContexts();
        } catch (Exception e) {
            e.printStackTrace();
            Ui.alertAndExit("Could not load context data from the database");
            return;
        }

        for (Context ctx: ctxList) {
            hostSelect.getItems().add(ctx.hostname);
            if (ctx.isDefault) {
                hostSelect.setValue(ctx.hostname);
            }
        }
    }

    @FXML private void applyHost() {

        String host = hostSelect.getValue();
        if (host == null) { return; }

        App.data.context.hostname = host;

        App.progress.startProgressTimer(Net.HTTP_REQUEST_TIMEOUT);

        App.data.net.testConnection()
            .thenAccept(isOnline -> App.data.getOrgUnits())
            .thenAccept(ok -> {
                App.progress.stopProgressTimer();
                Platform.runLater(() ->
                    App.primaryController.setBodyContent("login"));
            });
    }
}


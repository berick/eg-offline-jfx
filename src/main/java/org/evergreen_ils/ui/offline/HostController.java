package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class HostController {
    @FXML ComboBox<String> hostSelect;

    @FXML private void initialize() {

        List<Context> ctxList;

        try {
            ctxList = App.database.loadKnownContexts();
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

        Context ctx = App.context;
        ctx.hostname = host;

        App.progress.startProgressTimer(100);

        App.net.testConnection(ctx, isOnline -> handlePostNetCheck(isOnline));
    }

    Void handlePostNetCheck(boolean isOnline) {

        OrgUnit.getOrgUnits(App.context, p -> {
            App.progress.stopProgressTimer();
            App.primaryController.setBodyContent("login");
            return null;
        });

        return null;
    }
}


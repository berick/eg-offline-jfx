package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HostController {
    @FXML ComboBox<String> hostSelect;

    @FXML private void initialize() {

        List<String> seenHosts = new ArrayList<>();

        // Could be multiple contexts for a given host.

        List<Context> contexts = App.data.knownContexts.stream()
            .filter(c -> c.isDefault)
            .collect(Collectors.toList());

        if (!contexts.isEmpty()) {
            Context ctx = contexts.get(0);
            seenHosts.add(ctx.hostname);
            hostSelect.getItems().add(ctx.hostname);
            hostSelect.setValue(ctx.hostname);
        }

        // Now add the rest.
        for (Context ctx: App.data.knownContexts) {
            if (!seenHosts.contains(ctx.hostname)) {
                hostSelect.getItems().add(ctx.hostname);
                seenHosts.add(ctx.hostname);
            }
        }
    }

    @FXML private void applyHost() {
        App.logger.info("applyHost()");

        String host = hostSelect.getValue();
        if (host == null) { return; }

        // TODO insert into config DB unless this host is already
        // represented.

        App.data.context.hostname = host;
        App.primaryController.setStatusLabel();

        App.progress.startProgressTimer(Net.HTTP_REQUEST_TIMEOUT);

        App.data.net.testConnection().thenAccept(isOnline -> {
            App.data.getOrgUnits().thenAccept(ok -> {
                App.logger.info("applyHost() post org units");
                App.progress.stopProgressTimer();
                Platform.runLater(() ->
                    App.primaryController.setBodyContent("login"));
            });
        });
    }
}


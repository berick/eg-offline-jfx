package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class LoginController {

    @FXML TextField usernameInput;
    @FXML PasswordField passwordInput;
    @FXML ChoiceBox<String> workstationSelect;
    @FXML Text startupHost;

    @FXML private void initialize() {

        App.logger.info(
            "Launching login form with isOnline = " + App.data.net.status.isOnline);

        startupHost.setText(App.data.context.hostname);

        // Limit to contexts that match the selected host.
        List<Context> contexts = App.data.knownContexts.stream()
            .filter(c -> c.hostname.equals(App.data.context.hostname))
            .collect(Collectors.toList());

        for (Context ctx: contexts) {
            workstationSelect.getItems().add(ctx.workstation);
            if (ctx.isDefault) {
                workstationSelect.setValue(ctx.workstation);
            }
        }

        if (!App.data.net.status.isOnline) {
            // We cannot login if we are offline
            usernameInput.setDisable(true);
            passwordInput.setDisable(true);
        }
    }

    @FXML private void login() {

        String ws = workstationSelect.getValue();
        String host = App.data.context.hostname;

        if (ws == null) {
            if (App.data.net.status.isOnline) {
                App.data.context.hostname = host;
            } else {
                Error.alertAndExit(null, App.string("error.data.no_workstation"));
                return;
            }

        } else {

            // Find the selected context
            for (Context ctx: App.data.knownContexts) {
                if (ctx.hostname.equals(host) &&
                    ctx.workstation.equals(ws)) {
                    App.data.context = ctx;
                    App.primaryController.setStatusLabel();
                }
            }
        }

        // These are only tracked in memory
        // Will be null if there's no network.
        App.data.context.username = usernameInput.getText();
        App.data.context.password = passwordInput.getText();

        if (ws == null) {

            // TODO in this App.primaryController.setStatusLabel();
            App.primaryController.setBodyContent("workstations");

        } else {

            // Always refresh server values after a login.
            App.data.loadServerData()

                .thenAccept(x -> {
                    Platform.runLater(() ->
                        App.primaryController.setBodyContent("actions"));
                })

                // Failing to fetch offline data while online suggests
                // a server problem beyond just loss of network, but
                // at this point we have enough information to allow
                // offline transaction collection.
                .exceptionally(x -> {
                    Platform.runLater(() ->
                        App.primaryController.setBodyContent("actions"));
                    return null;
                });
        }
    }
}

package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.fxml.FXML;
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

        App.logger.info("Launching login form with isOnline = " + App.data.net.status.isOnline);

        startupHost.setText(App.data.context.hostname);

        List<Context> contexts = App.data.knownContexts.stream()
            .filter(c -> c.hostname.equals(App.data.context.hostname))
            .collect(Collectors.toList());


        // Load contexts from Data that match the selected hostname.
        // If one of them is also the default, copy its values into
        // App.data.context and set the workstation selector to the default workstation.

        for (Context ctx: contexts) {
            workstationSelect.getItems().add(ctx.workstation);
            if (ctx.isDefault) {
                workstationSelect.setValue(ctx.workstation);
            }
        }

        if (!App.data.net.status.isOnline) {
            // We cannot login if we are offline, so avoid prompting for unneeded values.
            usernameInput.setDisable(true);
            passwordInput.setDisable(true);
        }
    }

    @FXML private void login() {

        /*
        String ws = workstationSelect.getValue();
        String host = App.data.activeConfig.getHostname();

        if (ws == null) {
            if (App.data.net.isOnline) {
                // Temp config until we have a workstation.
                App.data.activeConfig = new Config(host);
            } else {
                App.logger.severe(
                    "We have no workstation or network access to register one");
                return;
            }

        } else {

            for (Config config: App.data.configList) {
                if (config.getWorkstation().equals(ws)
                    && config.getHostname().equals(host)) {
                    App.data.activeConfig = config;
                }
            }
        }

        // These are only tracked in memory
        // Will be null if there's no network.
        App.data.activeConfig.setUsername(usernameInput.getText());
        App.data.activeConfig.setPassword(passwordInput.getText());

        if (ws == null) {
            App.setRoot("workstations");
        } else {
            // Always refresh server values after a login.
            App.data.loadServerData();
            App.setRoot("primary");
        }
        */
    }
}

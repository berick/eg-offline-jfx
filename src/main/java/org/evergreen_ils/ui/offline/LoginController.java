package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class LoginController {

    @FXML TextField usernameInput;
    @FXML PasswordField passwordInput;
    @FXML ChoiceBox<String> workstationSelect;
    @FXML Text startupHost;
    @FXML VBox mainVbox;

    @FXML private void initialize() {

        App.logger.fine("Launching login form with isOnline = " + App.net.isOnline);

        App.applyOnlineBorder(mainVbox);

        for (Config config: App.data.configList) {
            workstationSelect.getItems().add(config.getWorkstation());
        }
        startupHost.setText(App.data.startupHost);

        if (!App.net.isOnline) {
            // We cannot login if we are offline, so avoid prompting for unneeded values.
            usernameInput.setDisable(true);
            passwordInput.setDisable(true);
        }
    }

    @FXML private void login() throws java.io.IOException {

        String ws = workstationSelect.getValue();
        String host = App.data.startupHost;

        if (ws == null) {
            if (App.net.isOnline) {
                // TODO launch workstation registration page.
            } else {
                App.logger.severe(
                    "We have no workstation or network access to register one");
                return;
            }
        }

        for (Config config: App.data.configList) {
            if (config.getWorkstation().equals(ws)
                && config.getHostname().equals(host)) {
                App.data.activeConfig = config;
            }
        }

        // These are only tracked in memory
        // Will be null if there's no network.
        App.data.activeConfig.setUsername(usernameInput.getText());
        App.data.activeConfig.setPassword(passwordInput.getText());

        // Always refresh server values after a login.
        App.data.loadServerData();

        App.setRoot("primary");
    }

    @FXML private void exit() {
        Platform.exit();
    }
}

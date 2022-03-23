package org.evergreen_ils.ui.offline;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class LoginController {

    @FXML TextField usernameInput;
    @FXML PasswordField passwordInput;
    @FXML ChoiceBox<String> workstationSelect;
    @FXML Text startupHost;

    @FXML private void initialize() {
        for (Config config: App.data.configList) {
            workstationSelect.getItems().add(config.getWorkstation());
        }
        startupHost.setText(App.data.startupHost);
    }

    @FXML private void login() throws java.io.IOException {

        String ws = workstationSelect.getValue();
        String host = App.data.startupHost;

        App.logger.info("Launching login form with isOnline = " + App.net.isOnline);

        for (Config config: App.data.configList) {
            if (config.getWorkstation().equals(ws)
                && config.getHostname().equals(host)) {
                App.data.activeConfig = config;
            }
        }

        if (App.data.activeConfig == null) {
            // User failed to select needed values.
            return;
        }

        // These are only tracked in memory
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

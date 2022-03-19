package org.evergreen_ils.ui.offline;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.List;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ChoiceBox;


public class LoginController {

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    @FXML TextField usernameInput;
    @FXML PasswordField passwordInput;
    @FXML ChoiceBox<String> hostnameSelect;
    @FXML ChoiceBox<String> workstationSelect;

    @FXML private void initialize() {
        for (Config config: App.data.configList) {
            workstationSelect.getItems().add(config.getWorkstation());
            hostnameSelect.getItems().add(config.getHostname());
        }
    }

    @FXML private void login() throws java.io.IOException {

        App.data.username = usernameInput.getText();
        App.data.password = passwordInput.getText();
        
        String ws = workstationSelect.getValue();
        String host = hostnameSelect.getValue();

        for (Config config: App.data.configList) {
            if (config.getWorkstation().equals(ws)
                && config.getHostname().equals(host)) {
                App.data.activeConfig = config;
            }
        }

        // Always refresh server values after a login.
        App.data.loadServerData();

        App.setRoot("primary");
    }

    @FXML private void exit() {
        Platform.exit();
    }

}

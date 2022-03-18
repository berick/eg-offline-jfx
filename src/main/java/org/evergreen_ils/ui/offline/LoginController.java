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
    @FXML ChoiceBox<String> workstationSelect;

    @FXML private void initialize() {
        for (Config config: Data.configList) {
            workstationSelect.getItems().add(config.getWorkstation());
        }
    }

    @FXML private void login() {
    }

    @FXML private void exit() {
        Platform.exit();
    }

}

package org.evergreen_ils.ui.offline;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class HostController {
    @FXML ChoiceBox<String> hostnameSelect;

    @FXML private void initialize() {
        for (Config config: App.data.configList) {
            hostnameSelect.getItems().add(config.getHostname());
        }
    }

    @FXML void setHost(ActionEvent action) throws IOException {
        App.data.startupHost = hostnameSelect.getValue();
         
        if (!App.net.canConnect(App.data.startupHost)) {
            App.logger.info("Cannot connect to network.  Going offline");
        }

        App.setRoot("login");
    }
}

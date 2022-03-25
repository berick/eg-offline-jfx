package org.evergreen_ils.ui.offline;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class HostController {
    @FXML ComboBox<String> hostnameSelect;

    @FXML private void initialize() {
        for (Config config: App.data.configList) {
            hostnameSelect.getItems().add(config.getHostname());
            if (config.getIsDefault()) {
                hostnameSelect.setValue(config.getHostname());
            }
        }
    }

    @FXML void setHost(ActionEvent action) throws IOException {
        String host = hostnameSelect.getValue();
        App.data.activeConfig = new Config(host);

        App.logger.info("User entered hostname value of " + host);
         
        if (!App.net.canConnect(host)) {
            App.logger.info("Cannot connect to network.  Going offline");
        }

        App.data.loadOrgUnits();

        App.setRoot("login");
    }
}

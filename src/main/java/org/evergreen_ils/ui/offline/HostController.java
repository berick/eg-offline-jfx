package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class HostController {
    @FXML ComboBox<String> hostSelect;

    @FXML private void initialize() {
        // TODO pull options from the database
        // TODO set the default value from what's in the DB
    }

    @FXML private void applyHost() {

        String host = hostSelect.getValue();
        if (host == null) { return; }

        App.context.hostname = host;
        // Update config
        // connect to network
        // Load org units from network OR file
        // Set login page as root.
    }
}


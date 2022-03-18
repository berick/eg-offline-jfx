package org.evergreen_ils.ui.offline;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.List;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;


public class PrimaryController {

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    @FXML private void initialize() throws java.sql.SQLException {


        List<Config> configs = Data.getConfigOptions();

        if (configs.size() == 0 || Data.activeConfig == null) {
            // TODO prompt for host+workstation
        }

        logger.info("Using config: " + Data.activeConfig);

        Data.loadServerValues();
    }

    @FXML private void close(ActionEvent event) {
        Platform.exit();
    }
}

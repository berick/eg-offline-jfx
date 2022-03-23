package org.evergreen_ils.ui.offline;

import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class PrimaryController {

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    @FXML TabPane tabs;
    @FXML VBox mainVbox;

    @FXML private void initialize() throws java.sql.SQLException {

        App.applyOnlineBorder(mainVbox);

        List<Config> configs = App.data.getConfigOptions();

        if (configs.size() == 0 || App.data.activeConfig == null) {
            // TODO prompt for host+workstation
        }

        logger.info("Using config: " + App.data.activeConfig);

        //App.data.loadServerValues();
        // TODO after login

        //tabs.setVisible(false);
    }

    @FXML private void close(ActionEvent event) {
        Platform.exit();
    }
}

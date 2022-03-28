package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class HostController {
    @FXML ComboBox<String> hostSelect;

    @FXML private void initialize() {  
        List<Context> ctxList;
        try {
            ctxList = App.database.loadKnownContexts();
        } catch (Exception e) {
            e.printStackTrace();
            Ui.alertAndExit("Could not load context data from the database");
            return;
        }
        
        for (Context ctx: ctxList) {                             
            hostSelect.getItems().add(ctx.hostname);
            if (ctx.isDefault) {
                hostSelect.setValue(ctx.hostname);
            }                                                                 
        }
    }

    @FXML private void applyHost() {

        String host = hostSelect.getValue();
        if (host == null) { return; }

        App.context.hostname = host;

        App.net.testConnection(App.context, isOnline -> {
            if (isOnline) {
                App.logger.info("We are online");

            } else {
                App.logger.info("We are NOT online");
            }
            // Load org units from network OR file
            // Set login page as root.
            return null;
        });
    }
}


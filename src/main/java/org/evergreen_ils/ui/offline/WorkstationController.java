package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class WorkstationController {
    
    @FXML VBox mainVbox;
    @FXML ChoiceBox<OrgUnit> orgSelect;
    @FXML TextField workstationInput;

    @FXML private void initialize() {
        App.logger.fine("Launching login form with isOnline = " + App.net.isOnline);

        App.applyOnlineBorder(mainVbox);
        buildOrgSelect(OrgUnit.orgTree);
    }

    void buildOrgSelect(OrgUnit org) {
        orgSelect.getItems().add(org);
        for (OrgUnit child: org.children) {
            buildOrgSelect(child);
        }
    }

    // TODO set default config from registered workstation
    //App.data.loadServerData();
    //App.setRoot("primary"); 
}

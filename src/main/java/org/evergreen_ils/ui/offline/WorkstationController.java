package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class WorkstationController {

    @FXML VBox mainVbox;
    @FXML ChoiceBox<OrgUnit> orgSelect;
    @FXML TextField workstationInput;
    @FXML Text hostname;

    @FXML private void initialize() {
        hostname.setText(App.data.context.hostname);
        buildOrgSelect(App.data.orgTree);
    }

    void buildOrgSelect(OrgUnit org) {
        orgSelect.getItems().add(org);
        for (OrgUnit child: org.children) {
            buildOrgSelect(child);
        }
    }

    @FXML void registerWorkstation() throws java.io.IOException {
        if (orgSelect.getValue() == null) { return; }

        /*
        App.net.registerWorkstation(
            App.data.activeConfig,
            orgSelect.getValue().id,
            workstationInput.getText()
        );

        App.data.setDefaultConfig();
        App.setRoot("login");
        */
    }
}



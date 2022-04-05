package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.json.JSONTokener;
import org.json.JSONObject;

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

        String ws = workstationInput.getText();
        OrgUnit org = orgSelect.getValue();

        if (ws == null || "".equals(ws) || org == null) { return; }

        App.data.context.workstation = String.format("%s-%s", org.shortname, ws);
        App.data.context.orgUnitId = org.id;

        App.data.net.registerWorkstation()
            .thenAccept(json -> {

                App.logger.info("Register workstation returned: " + json);

                JSONObject hash = new JSONObject(json);
                int registerOk = hash.getInt("register_ok");

                if (registerOk == 0) {
                    Error.alertAndExit(null, "Workstation registration failed: " + json);
                    return;
                }

                App.data.context.isDefault = true;

                App.data.saveContext();
                App.primaryController.setStatusLabel();

                // Once we have a workstation, return to the
                // login page so we can login with it.
                Platform.runLater(() ->
                    App.primaryController.setBodyContent("login")
                );
            });

    }
}



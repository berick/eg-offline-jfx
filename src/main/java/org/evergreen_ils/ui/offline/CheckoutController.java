package org.evergreen_ils.ui.offline;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class CheckoutController {

    ChoiceBox<CircDuration> durationSelect;
    ChoiceBox<NonCatType> nonCatSelect;

    @FXML
    public void initialize() {
        System.out.println("INITIALIZE()");
    }
    
}

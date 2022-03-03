module org.evergreen_ils.ui.offline {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.evergreen_ils.ui.offline to javafx.fxml;
    exports org.evergreen_ils.ui.offline;
}

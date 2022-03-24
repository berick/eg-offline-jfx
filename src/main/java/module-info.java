module org.evergreen_ils.ui.offline {
    requires java.net.http;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive org.json;

    opens org.evergreen_ils.ui.offline to javafx.fxml;
    exports org.evergreen_ils.ui.offline;
}

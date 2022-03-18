module org.evergreen_ils.ui.offline {
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;

    opens org.evergreen_ils.ui.offline to javafx.fxml;
    exports org.evergreen_ils.ui.offline;
}

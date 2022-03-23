package org.evergreen_ils.ui.offline;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static String OFFLINE_SCHEMA_FILE = "offline-schema.sql";
    private static Scene scene;

    static final Net net = new Net();
    static final Data data = new Data();

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    @Override
    public void start(Stage stage) throws IOException {

        App.data.schemaUrl = getClass().getResource(OFFLINE_SCHEMA_FILE);

        try {

            App.data.connect();
            App.data.createDatabase();

        } catch (Exception e) {

            System.err.println("Caught another error: " + e);
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR,
                "Interface failed to launch.  See error logs for details");

            alert.showAndWait();
            Platform.exit();
        }

        scene = new Scene(loadFXML("primary"));

        scene.getStylesheets().add(getClass()
            .getResource("offline.css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        setRoot("host");
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        logger.info("Starting Offline UI");

        try {
            launch();
        } catch (Exception e) {

            System.err.println("Caught another error: " + e);
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR,
                "An unrecoverable error occurred.  See error logs for details");

            alert.showAndWait();
            Platform.exit();
        }
    }
}


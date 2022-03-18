package org.evergreen_ils.ui.offline;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.application.Platform;

import java.io.IOException;
import java.util.logging.*;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {

    private static String OFFLINE_SCHEMA_FILE = "offline-schema.sql";
    private static Scene scene;

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    @Override
    public void start(Stage stage) throws IOException {

        Data.schemaUrl = getClass().getResource(OFFLINE_SCHEMA_FILE);

        try {

            Data.connect();
            Data.createDatabase();

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

        setRoot("login");
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


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

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    static final Logger logger = Logger.getLogger("org.evergreen_ils.ui.offline");

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"));
        stage.setScene(scene);
        stage.show();
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

            Data.connect();
            Data.createDatabase();
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


package org.evergreen_ils.ui.offline;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.application.Platform;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

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

        try {

            Data.connect();
            Data.createDatabase();
            launch();

        } catch (Exception e) {

            System.err.println("Caught another error: " + e);
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR,
                "An unrecoverable error occurred.  Please restart the application.  See error logs for details");

            alert.showAndWait();
            Platform.exit();
        }
    }
}


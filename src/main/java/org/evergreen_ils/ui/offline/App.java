package org.evergreen_ils.ui.offline;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static String OFFLINE_CSS_FILE = "offline.css";

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    // There should only be one of these
    static PrimaryController primaryController;

    // Shared progress bar
    static ProgressController progress;

    // Locale strings
    static ResourceBundle strings;

    // Currently active context
    static Data data;

    static boolean shuttingDown = false;

    static URL getResource(String fileName) throws IOException {
        return App.class.getResource(fileName);
    }

    @Override
    public void start(Stage stage) {

        App.data = new Data();

        scene = new Scene(loadFXML("primary"));

        scene.getStylesheets().add(getClass()
            .getResource(OFFLINE_CSS_FILE).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(App.string("app.title"));
        stage.show();
    }

    static void shutdown() {
        shuttingDown = true;
        Platform.exit();
    }

    static String string(String label) {
        return App.strings.getString(label);
    }

    static void setRoot(String fxml) {
        scene.setRoot(loadFXML(fxml));
    }

    // Load/execute the specified FXML file by name, minus the .fxml suffix.
    static Parent loadFXML(String fxml) {

        fxml += ".fxml";

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(App.getResource(fxml));

            return fxmlLoader.load();

        } catch (Exception e) {
            e.printStackTrace();

            Ui.alertAndExit(
                String.format(App.string("error.fxml.file"), fxml)
            );
        }

        return null;
    }

    public static void main(String[] args) {
        launch();
    }
}


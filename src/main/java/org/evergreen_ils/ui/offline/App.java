package org.evergreen_ils.ui.offline;

import java.io.IOException;
import java.util.logging.Logger;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    static ResourceBundle strings;

    static URL getResource(String fileName) throws IOException {
        return App.class.getResource(fileName);
    }

    void initialize() {
    
        try {

            logger.info("Loading string bundle for locale " + Locale.getDefault());

            URL localeDir = App.getResource("locale");
            String path = localeDir.getFile();
		   // App.strings = ResourceBundle.getBundle(path + "strings");
		    App.strings = ResourceBundle.getBundle(
                "org.evergreen_ils.ui.offline.strings");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (App.strings == null) {
            App.logger.severe(
                "Failed to laod string bundle for locale " + Locale.getDefault()
            );
            Platform.exit();
        }

        // TODO connect db
    }

    @Override
    public void start(Stage stage) {

        initialize();

        scene = new Scene(loadFXML("primary"));

        scene.getStylesheets().add(getClass()
            .getResource("offline.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle(App.strings.getString("appTitle"));
        stage.show();

        //setRoot("host");
    }

    static void setRoot(String fxml) {
        scene.setRoot(loadFXML(fxml));
    }

    // Load/execute the specified FXML file by name, minus the .fxml suffix.
    private static Parent loadFXML(String fxml) {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(App.getResource(fxml + ".fxml"));
            return fxmlLoader.load();

        } catch (IOException e) {

            Ui.alertAndExit(
                String.format(
                    App.strings.getString("fxmlFileError"), 
                    fxml + ".fxml"
                )
            );
        }

        return null;
    }

    public static void main(String[] args) {
        launch();
    }
}


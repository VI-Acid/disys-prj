package at.powergrid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
    // JavaFX-Startmethode: initialisiert und zeigt das Hauptfenster (Stage)
    @Override
    // @Override markiert Methoden, die eine Methode der Superklasse überschreiben
    public void start(Stage stage) throws Exception {
        // 1. FXML laden: Pfad zur Layout-Datei im Ressourcen-Ordner
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/powergrid/hello-view.fxml"));
        // 2. Scene mit geladener UI, Breite=500, Höhe=600
        Scene scene = new Scene(loader.load(), 500, 600);
        // 3. Fenster-Titel setzen
        stage.setTitle("PowerGrid GUI");
        // 4. Scene auf die Stage setzen
        stage.setScene(scene);
        // 5. Fenster anzeigen
        stage.show();
    }

    // Hauptmethode: startet den JavaFX-Lifecycle
    public static void main(String[] args) {
        launch(args); // startet JavaFX-App
    }
}
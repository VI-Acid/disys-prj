package at.powergrid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class Main extends Application {
    // GUI läuft unabhängig von der API
    public static void main(String[] args) {
        launch(args); // startet JavaFX-App
    }

    @Override
    public void start(Stage stage) {
        Label label = new Label("Willkommen bei Powergrid!");
        Scene scene = new Scene(label, 400, 200);

        stage.setTitle("JavaFX Test");
        stage.setScene(scene);
        stage.show();
    }
}
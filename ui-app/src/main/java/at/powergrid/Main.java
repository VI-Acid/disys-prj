package at.powergrid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;



public class Main extends Application {
    // GUI läuft unabhängig von der API


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/powergrid/hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 500, 300);
        stage.setTitle("PowerGrid GUI");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // startet JavaFX-App
    }
}
package at.powergrid;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelloController {

    @FXML
    private TextArea textAreaOutput;

    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    protected void onLoadCurrentDataClick() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/current"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> Platform.runLater(() ->
                        textAreaOutput.setText("Antwort von /current:\n\n" + response)
                ))
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            textAreaOutput.setText("Fehler beim Laden: " + ex.getMessage()));
                    return null;
                });
    }
}

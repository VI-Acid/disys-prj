package at.powergrid;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HelloController {

    @FXML
    public TextArea textAreaOutput;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void onLoadCurrentDataClick(ActionEvent event) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            EnergyData data = mapper.readValue(response.body(), EnergyData.class);

            String formatted = String.format(
                    "Aktuelle Stunde: %s\nCommunity-Depletion: %.2f %%\nGrid-Portion: %.2f %%",
                    data.getHour(), data.getCommunityDepleted(), data.getGridPortion()
            );

            textAreaOutput.setText(formatted);
        } catch (Exception e) {
            textAreaOutput.setText("Fehler beim Laden: " + e.getMessage());
        }
    }

    @FXML
    public void onLoadHistoryClick(ActionEvent event) {
        try {
            // Beispiel-Zeitraum (kannst du ggf. über ein Eingabefeld steuerbar machen)
            String start = "2025-06-01T00:00:00";
            String end = "2025-06-16T23:59:59";

            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<EnergyData> history = mapper.readValue(response.body(), new TypeReference<>() {});

            StringBuilder sb = new StringBuilder();
            sb.append("Historische Daten:\n\n");
            for (EnergyData d : history) {
                sb.append(String.format(
                        "Stunde: %s | Community: %.2f %% | Grid: %.2f %%\n",
                        d.getHour(), d.getCommunityDepleted(), d.getGridPortion()
                ));
            }

            textAreaOutput.setText(sb.toString());

        } catch (Exception e) {
            textAreaOutput.setText("Fehler beim Laden: " + e.getMessage());
        }
    }
}

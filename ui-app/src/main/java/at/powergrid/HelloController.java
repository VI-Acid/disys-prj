package at.powergrid;

import at.powergrid.dto.EnergyData;
import at.powergrid.dto.HistoricalResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelloController {

    @FXML
    public TextArea textAreaOutput;

    @FXML
    public DatePicker startDate;

    @FXML
    public DatePicker endDate;

    @FXML
    public ComboBox<String> startHour;

    @FXML
    public ComboBox<String> endHour;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @FXML
    public void initialize() {
        for (int i = 0; i < 24; i++) {
            String hourStr = String.format("%02d:00", i);
            startHour.getItems().add(hourStr);
            endHour.getItems().add(hourStr);
        }
        startHour.getSelectionModel().select(0);
        endHour.getSelectionModel().select(0);
    }

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
                    "Aktuelle Stunde: %s\nCommunity Pool: %.2f %% verbraucht\nGrid Portion: %.2f %%",
                    data.getTimestamp(), data.getCommunityDepleted(), data.getGridPortion()
            );

            textAreaOutput.setText(formatted);
        } catch (Exception e) {
            textAreaOutput.setText("Fehler beim Laden: " + e.getMessage());
        }
    }

    @FXML
    public void onLoadHistoryClick(ActionEvent event) {
        try {
            // Datum und Uhrzeit kombinieren
            String start = formatDateTime(startDate, startHour);
            String end = formatDateTime(endDate, endHour);

            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            HistoricalResponse data = mapper.readValue(response.body(), HistoricalResponse.class);

            String output = String.format(
                    "Community produced: %.3f kWh\nCommunity used: %.3f kWh\nGrid used: %.3f kWh",
                    data.getCommunityProduced(), data.getCommunityUsed(), data.getGridUsed()
            );

            textAreaOutput.setText(output);

        } catch (Exception e) {
            textAreaOutput.setText("Fehler beim Laden: " + e.getMessage());
        }
    }

    private String formatDateTime(DatePicker datePicker, ComboBox<String> hourBox) {
        if (datePicker.getValue() == null || hourBox.getValue() == null) {
            throw new IllegalArgumentException("Bitte Datum und Uhrzeit auswählen.");
        }
        LocalDateTime dateTime = datePicker.getValue()
                .atTime(Integer.parseInt(hourBox.getValue().substring(0, 2)), 0);
        return dateTime.format(formatter);
    }

}

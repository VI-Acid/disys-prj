package at.powergrid;

import at.powergrid.dto.EnergyData;
import at.powergrid.dto.HistoricalResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
    private final ObjectMapper mapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public HelloController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // für OffsetDateTime
    }

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

            if (response.statusCode() == 200) {
                // parse JSON
            } else {
                textAreaOutput.setText("Fehler vom Server: " + response.body());
            }

            EnergyData data = mapper.readValue(response.body(), EnergyData.class);

            String formatted = String.format(
                    "Zeitpunkt: %s\nErzeugt: %.2f kWh\nVerbraucht: %.2f kWh\nVerbrauchsanteil: %.2f%%",
                    data.getTimestamp(), data.getProduced_kWh(), data.getUsed_kWh(), data.getPercentage()
            );

            textAreaOutput.setText(formatted);
        } catch (Exception e) {
            textAreaOutput.setText("Fehler beim Laden: " + e.getMessage());
        }
    }

    @FXML
    public void onLoadHistoryClick(ActionEvent event) {
        try {
            String start = formatDateTime(startDate, startHour);
            String end = formatDateTime(endDate, endHour);

            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // parse JSON
            } else {
                textAreaOutput.setText("Fehler vom Server: " + response.body());
            }
            HistoricalResponse data = mapper.readValue(response.body(), HistoricalResponse.class);

            String output = String.format(
                    "Community erzeugt: %.3f kWh\nCommunity verbraucht: %.3f kWh\nGrid genutzt: %.3f kWh",
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

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
    // Verbindet Felder und Methoden mit Elementen und Events in der FXML-Datei
    public Label labelCommunityPool;

    @FXML
    public Label labelGridPortion;

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

    // Java 11 HTTP-Client für REST-Aufrufe
    private final HttpClient client = HttpClient.newHttpClient();
    // Jackson-Mapper für JSON (mit JavaTimeModule) um JSON in Java Objekt zu konvertieren
    private final ObjectMapper mapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public HelloController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // für OffsetDateTime und LocalDateTime
        // man bekommt saubere JSON-Strings und kann JSON-Daten mit Zeitfeldern zuverlässig wieder in LocalDateTime & Co verwandeln
        // Ohne dieses Modul könnten Zeitangaben beim JSON-Mapping fehlschlagen
        // .registerModule(...) erweitert den ObjectMapper um zusätzliche Datentyp-Unterstützung
        // JavaTimeModule sorgt dafür, dass z.B. OffsetDateTime korrekt in JSON umgewandelt werden kann
    }

    @FXML
    // Wird automatisch aufgerufen, nachdem die FXML-Datei geladen und die @FXML-Felder injiziert wurden
    public void initialize() {
        for (int i = 0; i < 24; i++) {
            // Erstellt einen Text wie "00:00", "01:00", ..., "23:00"
            String hourStr = String.format("%02d:00", i);
            // Fügt diese Uhrzeit-Auswahl zur startHour-ComboBox hinzu
            startHour.getItems().add(hourStr);
            endHour.getItems().add(hourStr);
        }
        // Standart 00:00 Dropdown Uhrzeitfelder
        startHour.getSelectionModel().select(0);
        endHour.getSelectionModel().select(0);
    }

    @FXML
    // Event-Handler für den Button "Aktuelle Daten laden"
    public void onLoadCurrentDataClick(ActionEvent event) {
        try {
            // Aufbau des GET-Requests an das REST-API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            // Sendet den HTTP-Request an den Server (z.B. /energy/current) und wartet auf die Antwort
            // Die Antwort wird als String zurückgegeben (JSON-Text), nicht als Binär- oder Stream-Daten
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // JSON in DTO mappen
                EnergyData data = mapper.readValue(response.body(), EnergyData.class);

                // Labels mit Prozentwerten aktualisieren
                labelCommunityPool.setText(String.format("%.2f%%", data.getPercentage()));
                // gridPortion direkt aus `produced_kWh`
                labelGridPortion.setText(
                        String.format("%.2f%%", data.getProduced_kWh())
                );
            } else {
                labelCommunityPool.setText("Error");
                labelGridPortion.setText("Error");
                System.err.println("Server error: " + response.body());
            }
        } catch (Exception e) {
            labelCommunityPool.setText("Error");
            labelGridPortion.setText("Error");
            System.err.println("Exception: " + e.getMessage());
        }
    }


    @FXML
    // Event-Handler für den Button "Historische Daten laden"
    public void onLoadHistoryClick(ActionEvent event) {
        try {
            // Zusammensetzen der URL mit Query-Parametern
            String start = formatDateTime(startDate, startHour);
            String end = formatDateTime(endDate, endHour);
            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // JSON in DTO mappen
                HistoricalResponse data = mapper.readValue(response.body(), HistoricalResponse.class);

                // TextArea mit Ergebnissen befüllen
                String output = String.format(
                        "Community produced: %.3f kWh\nCommunity used: %.3f kWh\nGrid used: %.3f kWh",
                        data.getCommunityProduced(), data.getCommunityUsed(), data.getGridUsed()
                );
                textAreaOutput.setText(output);
            } else {
                textAreaOutput.setText("Error from server: " + response.body());
            }

        } catch (Exception e) {
            textAreaOutput.setText("Error during loading: " + e.getMessage());
        }
    }

    // Hilfsmethode: Formatiert das Datum und die Stunde als ISO-8601 String mit UTC-Offset
    private String formatDateTime(DatePicker datePicker, ComboBox<String> hourBox) {
        if (datePicker.getValue() == null || hourBox.getValue() == null) {
            throw new IllegalArgumentException("Please select both date and hour.");
        }
        LocalDateTime dateTime = datePicker.getValue()
                .atTime(Integer.parseInt(hourBox.getValue().substring(0, 2)), 0);
        return dateTime.atOffset(java.time.ZoneOffset.UTC).toString();
    }
}

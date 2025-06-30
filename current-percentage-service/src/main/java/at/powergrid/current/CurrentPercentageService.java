package at.powergrid.current;

import at.powergrid.entity.CurrentPercentageEntity;
import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.CurrentPercentageRepository;
import at.powergrid.repository.EnergyUsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
// @Service (= Klassensignatur): Spring setzt diese Klasse als Bean auf und macht sie für andere Klassen nutzbar autowirable (@Autowired)
public class CurrentPercentageService {

    private final CurrentPercentageRepository percentageRepository; // Zugriffsobjekt für die Tabelle CurrentPercentageEntity
    private final EnergyUsageRepository usageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Hilfsklasse von Jackson, um JSON in Java-Objekte zu konvertieren und umgekehrt

    // Konstruktor: Wird von Spring aufgerufen, um die Abhängigkeiten zu injizieren
    public CurrentPercentageService(CurrentPercentageRepository percentageRepository, EnergyUsageRepository usageRepository) {
        this.percentageRepository = percentageRepository;
        this.usageRepository = usageRepository;
    }

    // Rundet einen double-Wert auf 'scale' Nachkommastellen,Halb-up (d.h. .12345 → .123, .12355 → .124)
    private static double round(double value, int scale) {
        return new BigDecimal(value)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @RabbitListener(queues = "updateQueue")
    // Registriert diese Methode als Listener für Nachrichten von RabbitMQ-Queue „updateQueue“
    // AMQP legt einen Message-Listener-Container an, der alle String-Nachrichten aus updateQueue an diese Methode weiterreicht
    public void receiveUpdate(String message) {
        try {
            // 1. Debug-Log: eingehende Nachricht
            System.out.println("Received update: " + message);

            // 2. JSON parsen und Stunde extrahieren
            JsonNode json = objectMapper.readTree(message); // Konvertiert den JSON-String in ein (Java) JsonNode-Objekt
            LocalDateTime hour = LocalDateTime.parse(json.get("datetime").asText()).truncatedTo(ChronoUnit.HOURS); // Gruppiert die Zeit auf volle Stunde

            System.out.println("Searched hour in DB: " + hour);

            // 3. Datenbankabfrage für die aktuelle Stunde
            Optional<EnergyUsageEntity> usageOpt = usageRepository.findById(hour); // Holt Daten zur angegebenen Stunde (falls vorhanden)
            if (usageOpt.isEmpty()) {
                System.out.println("No data available for current hour.");
                return;
            }
            EnergyUsageEntity usage = usageOpt.get();

            // DB-Query: Holt die  Werte aus der Tabelle EnergyUsageEntity für die angegebene Stunde
            double communityProduced = usage.getCommunityProduced();
            double communityUsed = usage.getCommunityUsed();
            double gridUsed = usage.getGridUsed();

            // communityDepleted (Deckungsgrad, max. 100 %)
            double communityDepleted = 0.0;
            if (communityProduced > 0) {
                communityDepleted = (communityUsed / communityProduced) * 100.0;
                communityDepleted = Math.min(communityDepleted, 100.0); // sicherstellen, dass es nicht über 100% geht
            }

            // gridPortion (Anteil Netzbezug an Gesamtenergie)
            double totalSupplied = communityProduced + gridUsed;
            double gridPortion   = (totalSupplied > 0)
                    ? (gridUsed / totalSupplied) * 100.0
                    : 0.0;

            // Prozentwerte auf 3 Dezimalstellen runden
            communityDepleted = round(communityDepleted, 3);
            gridPortion      = round(gridPortion, 3);

            // 4. Entity füllen und speichern
            CurrentPercentageEntity percentage = new CurrentPercentageEntity();
            percentage.setHour(hour.truncatedTo(ChronoUnit.SECONDS));
            percentage.setCommunityDepleted(communityDepleted);
            percentage.setGridPortion(gridPortion);
            percentageRepository.save(percentage);

            System.out.printf("Percentage value saved: %.2f %% (Grid)%n", gridPortion);

        } catch (Exception e) {
            System.out.println("Error while processing the message: " + e.getMessage());
        }
    }

}

package at.powergrid.usage;

import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.EnergyUsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
// @Service (= Klassensignatur): Spring setzt diese Klasse als Bean auf und macht sie für andere Klassen nutzbar autowirable (@Autowired)
// (über Konstruktor-Injection) + Listener Mechanismus wird aktiviert
public class UsageService {

    private final EnergyUsageRepository usageRepository; // Schnittstelle für Zugriff auf die Datenbank
    private final RabbitTemplate rabbitTemplate; // ist ein Spring-Objekt, das für die Kommunikation mit RabbitMQ zuständig ist (AMQP-Protokoll)
    private final ObjectMapper objectMapper = new ObjectMapper(); // Hilfsklasse von Jackson, um JSON in Java-Objekte zu konvertieren und umgekehrt

    // Konstruktor: Wird von Spring aufgerufen, um die Abhängigkeiten zu injizieren
    public UsageService(EnergyUsageRepository usageRepository, RabbitTemplate rabbitTemplate) {
        this.usageRepository = usageRepository;
        this.rabbitTemplate = rabbitTemplate;
    }


    // Rundet einen double-Wert auf 'scale' Nachkommastellen, Halb-up (d.h. .12345 → .123, .12355 → .124).
    private static double round(double value, int scale) {
        return new BigDecimal(value)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @RabbitListener(queues = "energyQueue")
    // Methoden-Annotation: Registriert annotierte Methode als Message-Handler für RabbitMQ, Message-Listener-Container
    // wird gestartet der sich an der angegebenen Queue ("energyQueue") anmeldet und eingehende Nachrichten automatisch an diese Methode weitergibt
    public void handleMessage(String message) {
        try {
            System.out.println("==> RECEIVED raw JSON: " + message);
            // 1. JSON parsen
            JsonNode json = objectMapper.readTree(message); // Konvertiert den JSON-String in ein (Java) JsonNode-Objekt
            String type = json.get("type").asText(); // Extrahiert den Typ der Nachricht (z.B. "PRODUCER" oder "USER")
            String kWhString = json.get("kwh").asText(); // Extrahiert die kWh-Zahl als String
            double kWh = Double.parseDouble(kWhString); // Umwandlung von String zu Double, falls nötig
            LocalDateTime datetime = LocalDateTime.parse(json.get("datetime").asText()); // Konvertiert Zeitstempel in ein LocalDateTime-Objekt

            System.out.printf("==> Parsed type: %s | kWh: %.3f | datetime: %s%n", type, kWh, datetime);

            // 2. Aggregation auf Stundenbasis
            LocalDateTime hour = datetime.truncatedTo(ChronoUnit.HOURS); // Gruppiert alle Messages, die innerhalb derselben Stunde eintreffen

            // 3. Entity (bestehende Datenzeile) laden oder neu erstellen
            EnergyUsageEntity usage = usageRepository.findById(hour)
                    .orElseGet(() -> new EnergyUsageEntity(hour, 0.0, 0.0, 0.0));

            // 4. Je nach Nachrichtentyp aktualisieren
            if ("PRODUCER".equalsIgnoreCase(type)) {
                usage.setCommunityProduced(usage.getCommunityProduced() + kWh); // Erhöht die produzierte Energiemenge in der Community

                // Grid-Usage berechnen: nur wenn Community-Use > Produktion
                double rest = usage.getCommunityUsed() - usage.getCommunityProduced();
                if (rest > 0) {
                    usage.setGridUsed(rest);
                } else {
                    usage.setGridUsed(0.0); // keine Grid-Nutzung nötig
                }
            } else if ("USER".equalsIgnoreCase(type)) {
                usage.setCommunityUsed(usage.getCommunityUsed() + kWh);
                // Falls community pool nicht reicht → grid used erhöhen
                if (usage.getCommunityUsed() > usage.getCommunityProduced()) {
                    double diff = usage.getCommunityUsed() - usage.getCommunityProduced();
                    usage.setGridUsed(diff);
                }
            }

            // 5. Rundung auf 3 Nachkommastellen in der DB
            usage.setCommunityProduced(round(usage.getCommunityProduced(), 3));
            usage.setCommunityUsed(round(usage.getCommunityUsed(),       3));
            usage.setGridUsed(round(usage.getGridUsed(),                 3));

            // 6. Logging zum Debug (mit gerundeten Werten)
            System.out.printf(
                    "→ Nach Update (gerundet): communityProduced=%.3f, communityUsed=%.3f, gridUsed=%.3f%n",
                    usage.getCommunityProduced(),
                    usage.getCommunityUsed(),
                    usage.getGridUsed()
            );

            // 7. Persistenz (= dauerhafte Speicherung in Datenbank) aktualisierte Entity speichern
            usageRepository.save(usage); // CRUD Methode
            System.out.println("UsageService verarbeitet: " + message);

            // 8. UPDATE an updateQueue senden
            rabbitTemplate.convertAndSend("updateQueue", message);
            System.out.println("Update sent to updateQueue.");

            System.out.println("UsageService processed message successfully.");
        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
            // Fängt alle Exceptions ab, damit der Listener nicht ausfällt
        }
    }
}
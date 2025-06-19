package at.powergrid.usage;

import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.EnergyUsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UsageService {

    private final EnergyUsageRepository usageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UsageService(EnergyUsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }

    @RabbitListener(queues = "energyQueue")
    public void handleMessage(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String type = json.get("type").asText();
            double kWh = json.get("kwh").asDouble();
            LocalDateTime datetime = LocalDateTime.parse(json.get("datetime").asText());
            LocalDateTime hour = datetime.truncatedTo(ChronoUnit.HOURS);

            // Suche oder erstelle Datenzeile für diese Stunde
            EnergyUsageEntity usage = usageRepository.findById(hour)
                    .orElseGet(() -> new EnergyUsageEntity(hour, 0.0, 0.0, 0.0));

            if ("PRODUCER".equalsIgnoreCase(type)) {
                usage.setCommunityProduced(usage.getCommunityProduced() + kWh);
            } else if ("USER".equalsIgnoreCase(type)) {
                usage.setCommunityUsed(usage.getCommunityUsed() + kWh);
                // Falls community pool nicht reicht → grid used erhöhen
                if (usage.getCommunityUsed() > usage.getCommunityProduced()) {
                    double diff = usage.getCommunityUsed() - usage.getCommunityProduced();
                    usage.setGridUsed(diff);
                }
            }

            usageRepository.save(usage);
            System.out.println("UsageService verarbeitet: " + message);

        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        }
    }
}
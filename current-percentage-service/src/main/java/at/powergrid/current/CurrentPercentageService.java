package at.powergrid.current;

import at.powergrid.entity.CurrentPercentageEntity;
import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.CurrentPercentageRepository;
import at.powergrid.repository.EnergyUsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class CurrentPercentageService {

    private final CurrentPercentageRepository percentageRepository;
    private final EnergyUsageRepository usageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CurrentPercentageService(CurrentPercentageRepository percentageRepository, EnergyUsageRepository usageRepository) {
        this.percentageRepository = percentageRepository;
        this.usageRepository = usageRepository;
    }

    @RabbitListener(queues = "updateQueue")
    public void receiveUpdate(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);

            LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
            Optional<EnergyUsageEntity> usageOpt = usageRepository.findById(currentHour);

            if (usageOpt.isEmpty()) {
                System.out.println("Keine Daten für aktuelle Stunde vorhanden.");
                return;
            }

            EnergyUsageEntity usage = usageOpt.get();
            double communityProduced = usage.getCommunityProduced();
            double communityUsed = usage.getCommunityUsed();
            double gridUsed = usage.getGridUsed();

            double communityDepleted = 100.0;
            double gridPortion = (communityUsed > 0) ? (gridUsed / communityUsed * 100.0) : 0.0;

            CurrentPercentageEntity percentage = new CurrentPercentageEntity();
            percentage.setHour(currentHour);
            percentage.setCommunityDepleted(communityDepleted);
            percentage.setGridPortion(gridPortion);

            percentageRepository.save(percentage);

            System.out.printf("Prozentwert gespeichert: %.2f %% (Grid)%n", gridPortion);

        } catch (Exception e) {
            System.out.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        }
    }


    public int getCurrentPercentage() {
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        Optional<CurrentPercentageEntity> entityOpt = percentageRepository.findById(currentHour);

        if (entityOpt.isPresent()) {
            double gridPortion = entityOpt.get().getGridPortion();
            return (int) Math.round(gridPortion);
        }

        return -1; // Wenn noch kein Wert vorhanden ist
    }

}

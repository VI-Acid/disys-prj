package at.powergrid.current;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CurrentPercentageService {

    private int totalProduction = 0;
    private int totalUsage = 0;


    private final ObjectMapper objectMapper = new ObjectMapper();


    public synchronized int getCurrentPercentage() {
        if (totalProduction == 0) return -1;
        return Math.min(100, (int) ((double) totalUsage / totalProduction * 100));
    }

    @RabbitListener(queues = "updateQueue")
    public void receiveUpdate(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            int production = json.get("totalProduction").asInt();
            int usage = json.get("totalUsage").asInt();

            this.totalProduction = production;
            this.totalUsage = usage;

            if (production == 0) {
                System.out.println("Achtung: Produktion = 0, Prozentwert nicht berechenbar.");
                return;
            }

            double percentage = ((double) usage / production) * 100;
            System.out.printf("Aktueller Verbrauchsanteil: %.2f %% (Usage: %d / Production: %d)%n", percentage, usage, production);
        } catch (Exception e) {
            System.out.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        }
    }

}

package at.powergrid.usage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

    @Service
    public class UsageService {

        private final RabbitTemplate rabbitTemplate;
        private final ObjectMapper objectMapper = new ObjectMapper();

        private int totalProduction = 0;
        private int totalUsage = 0;

        public UsageService(RabbitTemplate rabbitTemplate) {
            this.rabbitTemplate = rabbitTemplate;
        }

        @RabbitListener(queues = "energyQueue")
        public void handleMessage(String message) {
            try {
                JsonNode json = objectMapper.readTree(message);
                String type = json.get("type").asText();
                int kWh = json.get("kWh").asInt();

                if ("PRODUCER".equalsIgnoreCase(type)) {
                    totalProduction += kWh;
                } else if ("USER".equalsIgnoreCase(type)) {
                    totalUsage += kWh;
                }

                // Update-Nachricht an CurrentPercentageService senden
                String update = String.format("{\"totalProduction\":%d, \"totalUsage\":%d}", totalProduction, totalUsage);
                rabbitTemplate.convertAndSend("updateQueue", update);

                System.out.println("Nachricht empfangen: " + message);
                System.out.println("Update gesendet: " + update);

            } catch (Exception e) {
                System.out.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
            }
        }
    }


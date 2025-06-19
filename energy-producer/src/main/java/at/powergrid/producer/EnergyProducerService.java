package at.powergrid.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public EnergyProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 3000) // alle 3 Sekunden
    public void sendProductionMessage() {
        double weatherFactor = getWeatherFactor();
        double baseProduction = 0.002 + (0.008 * random.nextDouble());
        double adjustedProduction = Math.round((baseProduction * weatherFactor) * 10000.0) / 10000.0;

        String message = String.format(
                "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\",\"kwh\":%.4f,\"datetime\":\"%s\"}",
                adjustedProduction,
                LocalDateTime.now()//.toString()
        );

        rabbitTemplate.convertAndSend("energyQueue", message);
        System.out.println("Producer sent: " + message);
    }

    private double getWeatherFactor() {
        int hour = LocalDateTime.now().getHour();

        if (hour >= 10 && hour <= 16) {
            return 1.5; // sonnig: viel Produktion
        } else if (hour >= 7 && hour <= 9 || hour >= 17 && hour <= 19) {
            return 1.0; // morgens/abends: mittel
        } else {
            return 0.6; // nachts: wenig
        }
    }
}

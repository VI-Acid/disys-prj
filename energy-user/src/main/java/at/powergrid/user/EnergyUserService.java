package at.powergrid.user;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Service
public class EnergyUserService {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public EnergyUserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 3000)
    public void sendUsageMessage() {
        double usageFactor = getTimeBasedUsageFactor();
        double baseUsage = 0.001 + (0.005 * random.nextDouble());
        double adjustedUsage = Math.round((baseUsage * usageFactor) * 10000.0) / 10000.0;

        String message = String.format(Locale.US,
                "{\"type\":\"USER\",\"association\":\"COMMUNITY\",\"kwh\":\"%.4f\",\"datetime\":\"%s\"}",
                adjustedUsage,
                LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)
        );
        rabbitTemplate.convertAndSend("energyQueue", message);
        System.out.println("Sent user message: " + message);
    }

    private double getTimeBasedUsageFactor() {
        int hour = LocalDateTime.now().getHour();

        if (hour >= 6 && hour <= 9) {
            return 1.6; // morgens: hohe Nachfrage
        } else if (hour >= 17 && hour <= 21) {
            return 1.8; // abends: Spitzenzeit
        } else if (hour >= 10 && hour <= 16) {
            return 1.2; // tagsüber
        } else {
            return 0.7; // nachts: geringer Verbrauch
        }
    }



}

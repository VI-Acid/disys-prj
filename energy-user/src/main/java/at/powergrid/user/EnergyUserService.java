package at.powergrid.user;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

// @Service: Spring setzt diese Klasse als Bean auf und macht sie für andere Klassen nutzbar autowirable (@Autowired) für Verbrauchssimulation
@Service
public class EnergyUserService {

    private final RabbitTemplate rabbitTemplate; // ist ein Spring-Objekt, das für die Kommunikation mit RabbitMQ zuständig ist (AMQP-Protokoll)
    private final Random random = new Random(); // erzeugt zufällige Basis-Verbrauchswerte

    // Konstruktor, der RabbitTemplate injiziert
    public EnergyUserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // @Scheduled: Diese Methode wird alle 3 Sekunden aufgerufen, um simulierte Verbrauchsdaten zu senden
    @Scheduled(fixedDelay = 3000)
    public void sendUsageMessage() {
        double usageFactor = getTimeBasedUsageFactor();
        double baseUsage = 0.001 + (0.005 * random.nextDouble());
        double adjustedUsage = Math.round((baseUsage * usageFactor) * 10000.0) / 10000.0;

        String message = String.format(Locale.US,
                "{\"type\":\"USER\",\"association\":\"COMMUNITY\",\"kwh\":\"%.3f\",\"datetime\":\"%s\"}",
                adjustedUsage,
                LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)
        );
        rabbitTemplate.convertAndSend("energyQueue", message); // sendet die Nachricht an die RabbitMQ-Queue "energyQueue"
        System.out.println("Sent user message: " + message); // gibt die gesendete Nachricht in der Konsole aus
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

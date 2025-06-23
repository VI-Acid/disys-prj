package at.powergrid.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

// @Service: Spring setzt diese Klasse als Singleton-Bean auf und macht sie autowirable.
@Service
public class EnergyProducerService {
    // Verantwortlich für das Erzeugen und Versenden von Energie-Messages

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random(); // Hilft, Basis-Produktion zufällig zu variieren

    // Konstruktor-Injection: Spring übergibt automatisch das korrekte RabbitTemplate-Objekt
    public EnergyProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // @Scheduled = Spring-Annotation für wiederkehrende Tasks.
    @Scheduled(fixedDelay = 3000) // alle 3 Sekunden
    public void sendProductionMessage() {
        double weatherFactor = getWeatherFactor();
        double baseProduction = 0.002 + (0.008 * random.nextDouble());
        double adjustedProduction = Math.round((baseProduction * weatherFactor) * 10000.0) / 10000.0;

        // Baut einen JSON-String
        String message = String.format(Locale.US,
                "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\",\"kwh\":\"%.3f\",\"datetime\":\"%s\"}",
                adjustedProduction,
                LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString()
        );

        rabbitTemplate.convertAndSend("energyQueue", message); // Schickt den String in die RabbitMQ-Queue mit dem Namen energyQueue.
        System.out.println("Producer sent: " + message); // Konsolen-Log zur Laufzeit, damit ihr seht, was gesendet wird.
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

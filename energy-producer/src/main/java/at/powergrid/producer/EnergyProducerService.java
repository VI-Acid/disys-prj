package at.powergrid.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class EnergyProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public EnergyProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 3000) // alle 3 Sekunden
    public void sendProductionMessage() {
        double kWh = 0.002 + (0.008 * random.nextDouble()); // z.B. zwischen 0.002 und 0.010

        String message = String.format(
                "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\",\"kwh\":%.4f,\"datetime\":\"%s\"}",
                kWh,
                LocalDateTime.now().toString()
        );

        rabbitTemplate.convertAndSend("energyQueue", message);
        System.out.println("Producer sent: " + message);
    }
}


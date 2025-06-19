package at.powergrid.user;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        double kWh = 0.001 + (0.005 * random.nextDouble()); // z.B. 0.001 bis 0.006 kWh
        String message = String.format(
                "{\"type\":\"USER\",\"association\":\"COMMUNITY\",\"kwh\":%.4f,\"datetime\":\"%s\"}",
                kWh,
                LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend("energyQueue", message);
        System.out.println("Sent user message: " + message);
    }
}

package at.powergrid.producer;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EnergyProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnergyProducerApplication.class, args);
    }

    @Bean
    public Queue energyQueue() {
        return new Queue("energyQueue", false); // gleiche Queue wie beim User
    }
}


package at.powergrid.usage;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsageApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsageApplication.class, args);
    }

    @Bean
    public Queue energyQueue() {
        return new Queue("energyQueue", false);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue("updateQueue", false);
    }
}

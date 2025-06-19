package at.powergrid.current;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "at.powergrid.repository")
@EntityScan(basePackages = "at.powergrid.entity")
public class CurrentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrentApplication.class, args);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue("updateQueue", false); // gleiche Queue wie vom UsageService
    }
}

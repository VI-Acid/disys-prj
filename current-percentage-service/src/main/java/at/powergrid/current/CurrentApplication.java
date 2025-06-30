package at.powergrid.current;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Kombiniert @Configuration, @EnableAutoConfiguration und @ComponentScan
// Stellt Auto-Configuration und Component-Scanning für alle Beans im Paket at.powergrid.current und darunter sicher
@EnableJpaRepositories(basePackages = "at.powergrid.repository")
// Aktiviert das Scannen und Initialisieren von Spring Data JPA Repositories im angegebenen Package
@EntityScan(basePackages = "at.powergrid.entity")
// Stellt sicher, dass die JPA-Entities im angegebenen Package erkannt und registriert werden
public class CurrentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrentApplication.class, args);
    }

    @Bean
    // Erzeugt und registriert eine RabbitMQ-Queue namens updateQueue (nicht langlebig)
    // wird vom CurrentPercentageService per @RabbitListener überwacht
    public Queue updateQueue() {
        return new Queue("updateQueue", false); // gleiche Queue wie vom UsageService
    }


}

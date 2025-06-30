package at.powergrid.usage;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Aktiviert Auto-Konfiguration, Component-Scan und weitere Spring-Boot-Features
@EnableJpaRepositories(basePackages = "at.powergrid.repository")
// Schaltet die Verarbeitung von @RabbitListener-Methoden ein
@EntityScan(basePackages = "at.powergrid.entity")
// gewährleistet, dass diese Klassen beim Start erkannt, im JPA-Kontext registered und die entsprechende Tabellen-Mapping
// konfiguriert werden.
public class UsageApplication {

    // Startet den Spring-Boot-Kontext und damit alle Beans
    public static void main(String[] args) {
        SpringApplication.run(UsageApplication.class, args);
    }

    // Registriert RabbitMQ-Queue "energyQueue" (nicht persistent) für eingehende Messages
    @Bean
    public Queue energyQueue() {
        return new Queue("energyQueue", false);
    }

    // Legt eine RabbitMQ-Queue namens updateQueue (nicht persistent) im Broker an und registriert sie als Bean
    @Bean
    public Queue updateQueue() {
        return new Queue("updateQueue", false);
    }

}

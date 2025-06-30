package at.powergrid.user;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// Aktiviert Auto-Konfiguration, Component-Scan und weitere Spring‑Boot-Features
@EnableScheduling
// Erlaubt den Einsatz von @Scheduled-Methoden in Beans
public class UserApplication {

    // Startet den Spring‑Boot-Kontext
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    // @Bean queue(): Registriert RabbitMQ-Queue "energyQueue" (nicht persistent), damit Spring sie verwalten kann
    public Queue queue() {
        return new Queue("energyQueue", false);
    }
}

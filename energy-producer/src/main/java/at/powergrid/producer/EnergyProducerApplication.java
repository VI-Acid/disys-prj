package at.powergrid.producer;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
// Aktiviert Auto-Konfiguration, Component-Scan und weitere Spring‑Boot-Features
@EnableScheduling
// Erlaubt den Einsatz von @Scheduled-Methoden in Beans
public class EnergyProducerApplication {
    //  Startet den Spring‑Boot-Kontext
    public static void main(String[] args) {
        SpringApplication.run(EnergyProducerApplication.class, args);
    }

    @Bean
    // @Bean energyQueue(): Registriert RabbitMQ-Queue "energyQueue" (nicht persistent), damit Spring sie verwalten kann
    public Queue energyQueue() {
        return new Queue("energyQueue", false);
    }
}


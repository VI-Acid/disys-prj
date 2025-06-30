package at.powergrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Aktiviert Auto-Konfiguration, Component-Scan und weitere Spring‑Boot-Features
// Kombiniert @Configuration (Kennzeichnet die Klasse als Quelle für Bean-Definitionen), @EnableAutoConfiguration (
// sagt Spring Boot, dass es automatisch anhand der auf dem Klassenpfad gefundenen Bibliotheken (zB Spring MVC, JPA, AMQP) passende Konfigurationen
// („AutoConfigurations“) aktivieren soll) und @ComponentScan (Services, Repositories und Controller automatisch gefunden und ins Application Context übernommen)
// Scannt alle Komponenten (Controllers, Services, Repositories) unter at.powergrid
public class RestApiApplication {
    // Hauptklasse, die Spring Boot konfiguriert und startet

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
        // Startet den Spring-Application-Context, initialisiert Web-Server, registriert Beans und setzt alle konfigurierten Endpoints live
        // alle REST-Endpunkte sind danach über HTTP ereichbar zB /energy/current
    }
}


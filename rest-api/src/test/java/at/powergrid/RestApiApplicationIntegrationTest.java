package at.powergrid;

import at.powergrid.repository.CurrentPercentageRepository;
import at.powergrid.repository.EnergyUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

// MOCK (default): Lädt den Context und registriert einen Mock-Servlet-Environment, ohne echten HTTP-Listener
// RANDOM_PORT: Startet einen eingebetteten Web-Server auf einem zufälligen Port, so dass man echt HTTP-Requests gegen den Controller senden kann
// Es werden alle @Component, @Service, @Repository-Beans geladen, Auto-Configs ausgeführt, JPA-Repositories angebunden
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class RestApiApplicationIntegrationTest {

    @Autowired
    // bekommt man richtige JPA-Repositories
    TestRestTemplate rest; // vereinfachter HTTP-Client, kennt automatisch URL des laufenden Test-Servers und  übernimmt JSON (De-)Serialisierung über Jackson

    @Autowired
    EnergyUsageRepository usageRepo;

    @Autowired
    CurrentPercentageRepository pctRepo;

    @BeforeEach
        // Datenbankclean - löscht alle Daten, damit jeder Test in einem sauberen Zustand startet
    void cleanup() {
        usageRepo.deleteAll();
        pctRepo.deleteAll();
    }

    @Test
        // markiert eine Methode als Testmethode für JUnit
    void currentReturnsZeroWhenNoData() {
        // 1. Führe einen GET-Request gegen /energy/current aus und erhalte die Response als String
        ResponseEntity<String> resp = rest.getForEntity("/energy/current", String.class);

        // 2. Prüfe, dass der HTTP-Statuscode 200 (OK) ist
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);

        // 3. Prüfe, dass der JSON-Body den Teilstring "percentage":0 enthält
        // (d.h. bei leerer Datenbank soll der Prozentsatz 0 zurückgegeben werden)
        assertThat(resp.getBody()).contains("\"percentage\":0");
    }

    @Test
        // markiert eine Methode als Testmethode für JUnit (jeder Test folgt AAA-Pattern)
    void historicalAggregates() {
        // Arrange
        // 1. Erzeuge ein LocalDateTime-Objekt für den Stundeneintrag 2025-06-24 10:00
        var h = java.time.LocalDateTime.of(2025,6,24,10,0);

        // 2. Speichere eine EnergyUsageEntity in die Testdatenbank:
        //    communityProduced = 2.0 kWh, communityUsed = 1.0 kWh, gridUsed = 0.5 kWh
        usageRepo.save(new at.powergrid.entity.EnergyUsageEntity(h, 2.0, 1.0, 0.5));

        // 3. Baue die ISO-8601-Strings für die URL-Parameter start und end
        //    im UTC-Offset-Format, z.B. "2025-06-24T10:00Z" bzw. "2025-06-24T11:00Z"
        String start = h.atOffset(java.time.ZoneOffset.UTC).toString();
        String end   = h.plusHours(1).atOffset(java.time.ZoneOffset.UTC).toString();

        // Act
        // 4. Führe den GET-Request gegen /energy/historical?start=...&end=... aus
        ResponseEntity<String> resp = rest.getForEntity(
                "/energy/historical?start="+start+"&end="+end,
                String.class
        );

        // Assert
        // 5. Prüfe den HTTP-Statuscode auf 200 (OK)
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);

        // 6. Prüfe im JSON-Body die aggregierten Felder:
        //    communityProduced = 2.0 + (keine weiteren Einträge) = 2.0
        //    communityUsed     = 1.0 +  "" = 1.0
        //    gridUsed          = 0.5 +  "" = 0.5
        assertThat(resp.getBody())
                .contains("\"communityProduced\":2.0")
                .contains("\"communityUsed\":1.0")
                .contains("\"gridUsed\":0.5");
    }
}

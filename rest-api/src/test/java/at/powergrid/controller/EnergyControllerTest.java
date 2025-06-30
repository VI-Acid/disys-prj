package at.powergrid.controller;

import at.powergrid.dto.EnergyData;
import at.powergrid.dto.HistoricalResponse;
import at.powergrid.entity.CurrentPercentageEntity;
import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.CurrentPercentageRepository;
import at.powergrid.repository.EnergyUsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Unit Test (Web-Layer-Slice-Test)
// EnergyControllerTest lädt nur Web-Schicht (Controller, Konverter, Handler-Mapping)
// und erzeugt einen MockMvc (echte HTTP-GET-Requests gegen Controller simuliert)
// (Repositories), wird per @MockBean im Spring Context durch Mocks ersetzt. testet Routing (GET /energy/current)
// HTTP-Status und Header JSON-Format und Inhalt

@WebMvcTest(EnergyController.class)
// Lade nur den „Web“-Layer für EnergyController, ohne den kompletten Spring-Context
class EnergyControllerTest {

    @Autowired
    // Bitte injiziere mir hier ein Bean aus dem Context.
    // Im Test bekommt man zB das MockMvc und den Jackson-ObjectMapper automatisch bereitgestellt
    private MockMvc mvc;
    // Springs MockMvc simuliert HTTP-Aufrufe gegen den Controller

    @MockBean
    // Erstellt einen Mockito-Mock und ersetzt damit das echte Bean im Spring-Context
    // Test-Controller “denkt”, er ruft die echten Repositories auf, bekommt aber vorgegebene Mocks
    private EnergyUsageRepository usageRepository;
    // Ersetzt das echte Repository durch ein Mockito-Mock im Spring-Context

    @MockBean
    private CurrentPercentageRepository percentageRepository;
    // Gleiches für das CurrentPercentage-Repo

    @Test
    // markiert eine Methode als Testmethode für JUnit (jeder Test folgt AAA-Pattern)
    @DisplayName("GET /energy/current → returns JSON with communityDepleted and gridPortion")
        // JUnit5: Verleiht dem Test in der Ausgabe einen lesbaren Titel anstelle des Methodennamens

    void testGetCurrent() throws Exception {
        // 1. Stub: wenn findById aufgerufen wird, liefern wir eine Entity mit 42.5% und 17.5% Netzanteil
        LocalDateTime nowHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        CurrentPercentageEntity entity = new CurrentPercentageEntity();
        entity.setHour(nowHour);
        entity.setCommunityDepleted(42.5);
        entity.setGridPortion(17.5);
        when(percentageRepository.findById(ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Optional.of(entity));

        // 2. Führe den GET-Request aus und prüfe Response
        mvc.perform(get("/energy/current"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // das DTO hat das Feld „percentage“ → communityDepleted
                .andExpect(jsonPath("$.percentage").value(42.5))
                // wir haben gridPortion in produced_kWh gepackt
                .andExpect(jsonPath("$.produced_kWh").value(17.5))
                // timestamp prüfen, dass es ein ISO-Datum ist
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    // markiert eine Methode als Testmethode für JUnit (jeder Test folgt AAA-Pattern)
    @DisplayName("GET /energy/historical → adds usage values correctly")
        // JUnit5: Verleiht dem Test in der Ausgabe einen lesbaren Titel anstelle des Methodennamens
    void testGetHistorical() throws Exception {

        // 1. Zwei Stunden mit definierten usage-Werten
        LocalDateTime h1 = LocalDateTime.of(2025, 6, 24, 10, 0);
        LocalDateTime h2 = LocalDateTime.of(2025, 6, 24, 11, 0);
        EnergyUsageEntity e1 = new EnergyUsageEntity(h1, 1.0, 2.0, 0.5);
        EnergyUsageEntity e2 = new EnergyUsageEntity(h2, 0.5, 1.5, 0.2);

        when(usageRepository.findByHourBetween(
                ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(e1, e2));

        // 2. Baue Start‐ und End‐Parameter im ISO-Format
        String start = OffsetDateTime.of(h1, ZoneOffset.UTC).toString();
        String end   = OffsetDateTime.of(h2.plusHours(1), ZoneOffset.UTC).toString();

        // 3. Führe Request aus und prüfe Summen
        mvc.perform(get("/energy/historical")
                        .param("start", start)
                        .param("end", end))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // totalProduced = 1.0 + 0.5 = 1.5
                .andExpect(jsonPath("$.communityProduced").value(1.5))
                // totalUsed = 2.0 + 1.5 = 3.5
                .andExpect(jsonPath("$.communityUsed").value(3.5))
                // totalGrid = 0.5 + 0.2 = 0.7
                .andExpect(jsonPath("$.gridUsed").value(0.7));
    }
}

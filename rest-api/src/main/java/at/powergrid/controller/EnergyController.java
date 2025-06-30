package at.powergrid.controller;

import at.powergrid.dto.EnergyData;
import at.powergrid.dto.HistoricalResponse;
import at.powergrid.entity.CurrentPercentageEntity;
import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.CurrentPercentageRepository;
import at.powergrid.repository.EnergyUsageRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
// Kombiniert @Controller (als Bean beim Start registriert) und @ResponseBody: Alle Methoden liefern direkt HTTP-Response-Bodies
// @Controller = Als Bean beim Start registriert; markiert die Klasse als Web-Controller (eine Stelle, wo HTTP-Anfragen (z.B. GET/POST) verarbeitet werden)
// @ResponseBody = Der Rückgabewert der Methode wird direkt als HTTP-Antwort gesendet (z.B. als JSON oder Text, nicht als HTML-Seite oder View)
@RequestMapping("/energy")
// grundlegende Annotation in Spring , mit der HTTP-Anfragen auf Controller-Klassen oder -Methoden „geroutet“ werden
// Basispfad für alle Endpunkte in dieser Klasse (/energy = erreichbarkeit)
public class EnergyController {

    private final EnergyUsageRepository usageRepository;
    private final CurrentPercentageRepository percentageRepository;

    public EnergyController(EnergyUsageRepository usageRepository, CurrentPercentageRepository percentageRepository) {
        this.usageRepository = usageRepository;
        this.percentageRepository = percentageRepository;
    }

    // Nimmt HTTP-GET-Requests /energy/current entgegen → aktueller Prozentwert laut Tabelle
    // baut intern einen Eintrag im RequestMappingHandlerMapping auf und verbindet URL + HTTP-Methode mit deiner Java-Methode
    // "Go to declaration or usages" -> Zeigt, wo diese URL verwendet oder aufgerufen wird
    // "Generate request in HTTP Client" -> Erstellt Beispiel-Request fürs Testen direkt in IntelliJ
    // "Show all endpoints of module" -> Zeigt alle HTTP-Endpunkte dieser Klasse/Anwendung
    // "Generate OpenAPI draft" -> Erstellt Basis für OpenAPI-Dokumentation
    @GetMapping("/current")
    public EnergyData getCurrent() {
        // 1. Ermitteln der aktuellen Stunde (ohne Minuten/Sekunden)
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        // 2. Laden des Eintrags aus current_percentage (oder null, wenn nicht vorhanden)
        CurrentPercentageEntity entity = percentageRepository.findById(currentHour).orElse(null);

        double communityDepleted = 0.0;
        double gridPortion      = 0.0;

        if (entity != null) {
            // 3. Werte aus Entity übernehmen
            communityDepleted = entity.getCommunityDepleted();
            gridPortion      = entity.getGridPortion();
        }

        // 4. Zeitstempel als UTC-Offset
        OffsetDateTime ts = currentHour.atOffset(ZoneOffset.UTC);

        // 5. DTO erzeugen: ts, communityDepleted, gridPortion, produced/used kWh (hier nur prozentual)
        return new EnergyData(ts, communityDepleted, gridPortion, 0.0);
    }



    // GET /energy/historical?start=...&end=... → Gibt aggregierte Summen für einen Zeitraum zurück
    // Wird aufgerufen, wenn jemand per URL Daten mit ?start=...&end=... abfragt
    @GetMapping("/historical")
    public HistoricalResponse getHistoricalData(
            // 1. Query-Parameter start und end als ISO DATE_TIME
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {

        // 2. Holen aller Usage-Entities zwischen start und end
        List<EnergyUsageEntity> entries = usageRepository.findByHourBetween(start.toLocalDateTime(), end.toLocalDateTime());

        // 3. Summieren der kWh-Werte über alle Stunden
        //  .stream() -> wandelt die Liste in einen Datenstrom um, um sie funktional zu verarbeiten (z.B. filtern, summieren)
        // mapToDouble(...) -> holt den gewünschten double-Wert aus jedem Eintrag
        // :: Methoden-Referenz, z.B. obj -> obj.getCommunityProduced()
        // sum() -> summiert alle extrahierten Werte
        double totalProduced = entries.stream().mapToDouble(EnergyUsageEntity::getCommunityProduced).sum();
        double totalUsed     = entries.stream().mapToDouble(EnergyUsageEntity::getCommunityUsed).sum();
        double totalGrid     = entries.stream().mapToDouble(EnergyUsageEntity::getGridUsed).sum();

        // 4. DTO füllen
        HistoricalResponse response = new HistoricalResponse();
        response.setCommunityProduced(totalProduced);
        response.setCommunityUsed(totalUsed);
        response.setGridUsed(totalGrid);

        return response; // 5. JSON-Antwort mit Summen
    }
}

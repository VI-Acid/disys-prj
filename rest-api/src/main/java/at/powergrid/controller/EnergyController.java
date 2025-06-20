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
@RequestMapping("/energy")
@CrossOrigin(origins = "*")
public class EnergyController {

    private final EnergyUsageRepository usageRepository;
    private final CurrentPercentageRepository percentageRepository;

    public EnergyController(EnergyUsageRepository usageRepository, CurrentPercentageRepository percentageRepository) {
        this.usageRepository = usageRepository;
        this.percentageRepository = percentageRepository;
    }

    /** GET /energy/current → aktueller Prozentwert laut Tabelle */
    @GetMapping("/current")
    public EnergyData getCurrent() {
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        CurrentPercentageEntity entity = percentageRepository.findById(currentHour).orElse(null);

        if (entity == null) {
            return new EnergyData(currentHour.atOffset(ZoneOffset.UTC), 0.0, 0.0, 0.0, entity.getCommunityDepleted(), entity.getGridPortion());
            // Fallback
        }

        return new EnergyData(currentHour.atOffset(ZoneOffset.UTC), 0, 0, 0, entity.getCommunityDepleted(), entity.getGridPortion());
    }

    /** GET /energy/historical?start=...&end=... → Aggregierte Summen aus energy_usage */
    @GetMapping("/historical")
    public HistoricalResponse getHistoricalData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {

        List<EnergyUsageEntity> entries = usageRepository.findByHourBetween(start.toLocalDateTime(), end.toLocalDateTime());

        double totalProduced = entries.stream().mapToDouble(EnergyUsageEntity::getCommunityProduced).sum();
        double totalUsed     = entries.stream().mapToDouble(EnergyUsageEntity::getCommunityUsed).sum();
        double totalGrid     = entries.stream().mapToDouble(EnergyUsageEntity::getGridUsed).sum();

        HistoricalResponse response = new HistoricalResponse();
        response.setCommunityProduced(totalProduced);
        response.setCommunityUsed(totalUsed);
        response.setGridUsed(totalGrid);

        return response;
    }
}

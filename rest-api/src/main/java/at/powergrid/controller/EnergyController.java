package at.powergrid.controller;

import at.powergrid.dto.EnergyData;
import at.powergrid.dto.HistoricalResponse;
import at.powergrid.service.EnergyDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyDataService service;

    public EnergyController(EnergyDataService service) {
        this.service = service;
    }

    /** GET /energy/current → aktuellster Stunden-Prozentwert aus DB */
    @GetMapping("/current")
    public EnergyData getCurrent() {
        return service.getCurrentEnergyData();
    }

    /** GET /energy/historical?start=…&end=… */
    @GetMapping("/historical")
    public List<EnergyData> getHistoricalData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return EnergyDataService.getHistoricalEnergyData(start, end);
    }

}

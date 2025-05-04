package at.powergrid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;

@RestController
public class EnergyController {
    // REST-Schnittstelle mit HTTP-Endpunkten

    private final EnergyRepository repository;

    @Autowired
    public EnergyController(EnergyRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "REST API läuft!";
    }

    // aktuelle Stunde
    @GetMapping("/current")
    public EnergyData getCurrent() {
        EnergyData current = new EnergyData(
                LocalTime.now().getHour(),
                65,
                40
        );
        current.setId(EnergyRepository.nextId());
        repository.save(current);
        return current;
    }


    // alle bisherigen
    @GetMapping("/history")
    public List<EnergyData> getHistory() {
        return repository.findAll();
    }

    // Datensätze löschen
    @DeleteMapping("/history/{id}")
    public void delete(@PathVariable UUID id) {
        repository.remove(id);
    }

    // neuen Datensatz anlegen
    @PostMapping("/history")
    public EnergyData create(@RequestBody EnergyData data) {
        data.setId(EnergyRepository.nextId());
        return repository.save(data);
    }

    // Datensatz aktualisieren
    @PutMapping("/history/{id}")
    public EnergyData update(@PathVariable UUID id, @RequestBody EnergyData data) {
        data.setId(id);
        return repository.save(data);
    }
}
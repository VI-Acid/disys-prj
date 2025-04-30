package at.powergrid;

import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class EnergyDataController {

    @GetMapping("/current")
    public Map<String, Object> getCurrent() {
        return Map.of("hour", LocalTime.now().getHour(), "produced_kWh", 65, "used_kWh", 40);
    }

    @GetMapping("/history")
    public List<Map<String, Object>> getHistory() {
        return List.of(
                Map.of("hour", 9, "produced_kWh", 50, "used_kWh", 30),
                Map.of("hour", 10, "produced_kWh", 60, "used_kWh", 50)
        );
    }
}

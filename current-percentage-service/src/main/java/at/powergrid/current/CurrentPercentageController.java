package at.powergrid.current;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/current")
@CrossOrigin(origins = "*")
public class CurrentPercentageController {

    @Autowired
    private CurrentPercentageService percentageService;

    @GetMapping
    public String getPercentage() {
        int percent = percentageService.getCurrentPercentage();
        OffsetDateTime now = OffsetDateTime.now();

        if (percent == -1) {
            return "Noch keine Produktion vorhanden";
        }
        return percent + " %";
    }
}

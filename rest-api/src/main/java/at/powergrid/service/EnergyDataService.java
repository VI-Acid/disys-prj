package at.powergrid.service;

import at.powergrid.dto.EnergyData;
import at.powergrid.repository.EnergyProductionRepository;
import at.powergrid.repository.EnergyUsageRepository;
import at.powergrid.repository.CurrentPercentageRepository;
import at.powergrid.repository.EnergyProductionEntity;
import at.powergrid.repository.EnergyUsageEntity;
import at.powergrid.repository.CurrentPercentageEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnergyDataService {

    private static EnergyUsageRepository    usageRepo;
    private static EnergyProductionRepository productionRepo;
    private final CurrentPercentageRepository percentageRepo;

    public EnergyDataService(
            EnergyUsageRepository usageRepo,
            EnergyProductionRepository productionRepo,
            CurrentPercentageRepository percentageRepo
    ) {
        this.usageRepo      = usageRepo;
        this.productionRepo = productionRepo;
        this.percentageRepo = percentageRepo;
    }

    /** Holt den prozentualen Stand der aktuellen Stunde aus der percentage-Tabelle */
    public EnergyData getCurrentEnergyData() {
        CurrentPercentageEntity pct = percentageRepo.findTopByOrderByTimestampDesc();

        return new EnergyData(
                pct.getTimestamp().getHour(),
                pct.getPercentage());
    }

    /** Holt Usage + Production zwischen zwei Zeitpunkten */
    public static List<EnergyData> getHistoricalEnergyData(LocalDateTime start, LocalDateTime end) {
        List<EnergyUsageEntity> usageList = usageRepo.findByTimestampBetween(start, end);
        List<EnergyProductionEntity> prodList = productionRepo.findByTimestampBetween(start, end);

        Map<LocalDateTime, Double> usageMap = usageList.stream()
                .collect(Collectors.toMap(EnergyUsageEntity::getTimestamp, EnergyUsageEntity::getKwh));

        Map<LocalDateTime, Double> prodMap = prodList.stream()
                .collect(Collectors.toMap(EnergyProductionEntity::getTimestamp, EnergyProductionEntity::getKwh));

        Set<LocalDateTime> allHours = new HashSet<>();
        allHours.addAll(usageMap.keySet());
        allHours.addAll(prodMap.keySet());

        List<EnergyData> result = new ArrayList<>();
        for (LocalDateTime hour : allHours) {
            double used = usageMap.getOrDefault(hour, 0.0);
            double produced = prodMap.getOrDefault(hour, 0.0);
            int hourInt = hour.getHour(); // oder: hour.getHour() + hour.getDayOfMonth() * 100
            result.add(new EnergyData(hourInt, produced));
        }

        return result;
    }

}

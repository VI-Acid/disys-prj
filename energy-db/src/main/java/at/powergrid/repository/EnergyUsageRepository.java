package at.powergrid.repository;

import at.powergrid.entity.EnergyUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsageEntity, LocalDateTime> {

    // Holt alle Stunden zwischen zwei Zeitpunkten (z.B. für /historical)
    List<EnergyUsageEntity> findByHourBetween(LocalDateTime start, LocalDateTime end);
}

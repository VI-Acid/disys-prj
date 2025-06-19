package at.powergrid.repository;

import at.powergrid.repository.EnergyUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsageEntity, Long> {
    @Query("SELECT e FROM EnergyUsageEntity e WHERE e.timestamp >= :start AND e.timestamp < :end")
    EnergyUsageEntity findByHour(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

    List<EnergyUsageEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}

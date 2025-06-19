package at.powergrid.repository;

import at.powergrid.repository.EnergyProductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EnergyProductionRepository extends JpaRepository<EnergyProductionEntity, Long> {
    @Query("SELECT e FROM EnergyProductionEntity e WHERE e.timestamp >= :start AND e.timestamp < :end")
    EnergyProductionEntity findByHour(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

    List<EnergyProductionEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}

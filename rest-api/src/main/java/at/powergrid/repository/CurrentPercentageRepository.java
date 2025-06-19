package at.powergrid.repository;

import at.powergrid.repository.CurrentPercentageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {

    CurrentPercentageEntity findTopByOrderByTimestampDesc();
}

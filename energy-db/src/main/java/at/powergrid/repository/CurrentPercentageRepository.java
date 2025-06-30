package at.powergrid.repository;

import at.powergrid.entity.CurrentPercentageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

// Unsere Entity-Klasse CurrentPercentageEntity - JpaRepository aus Spring Data JPA, das grundlegende Repository-API
// LocalDateTime als Typ des Primärschlüssels
public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {

}

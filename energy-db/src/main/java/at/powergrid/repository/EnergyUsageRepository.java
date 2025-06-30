package at.powergrid.repository;

import at.powergrid.entity.EnergyUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

// public interface: sondern eine Schnittstelle.
// extends JpaRepository<EnergyUsageEntity, LocalDateTime>:
// EnergyUsageEntity ist die Entity, auf der das Repository operiert
// LocalDateTime ist der Typ des Primärschlüssels (@Id-Feld hour).
// Effekt: Spring erzeugt zur Laufzeit eine Implementierung mit Methoden wie save(), findById(), findAll(), deleteById(), etc. CRUD
public interface EnergyUsageRepository extends JpaRepository<EnergyUsageEntity, LocalDateTime> {

    // Holt alle Stunden zwischen zwei Zeitpunkten (für /historical)
    List<EnergyUsageEntity> findByHourBetween(LocalDateTime start, LocalDateTime end);
    // Spring Data JPA leitet aus dem Methodennamen automatisch eine JPQL/SQL-Query ab (SELECT e FROM energy_usage e
    // WHERE e.hour BETWEEN :start AND :end
}

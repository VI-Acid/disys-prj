package at.powergrid.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "energy_production")
public class EnergyProductionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private double kwh;

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getKwh() {
        return kwh;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }
}

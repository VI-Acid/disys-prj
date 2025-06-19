package at.powergrid.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "energy_usage")
public class EnergyUsageEntity {

    @Id
    @Column(nullable = false)
    private LocalDateTime hour; // z.B. 2025-01-10T14:00:00

    @Column(nullable = false)
    private double communityProduced;

    @Column(nullable = false)
    private double communityUsed;

    @Column(nullable = false)
    private double gridUsed;

    public EnergyUsageEntity(LocalDateTime hour, double produced, double used, double grid) {
        this.hour = hour;
        this.communityProduced = produced;
        this.communityUsed = used;
        this.gridUsed = grid;
    }

    public EnergyUsageEntity() {}

    // Getter & Setter

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }
}

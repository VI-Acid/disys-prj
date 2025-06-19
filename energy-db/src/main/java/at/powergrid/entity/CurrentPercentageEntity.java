package at.powergrid.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "current_percentage")
public class CurrentPercentageEntity {

    @Id
    @Column(nullable = false)
    private LocalDateTime hour;

    @Column(nullable = false)
    private double communityDepleted;

    @Column(nullable = false)
    private double gridPortion;

    public CurrentPercentageEntity() {
    }

    public CurrentPercentageEntity(LocalDateTime hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    // Getter & Setter

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public LocalDateTime getHour() {
        return hour;
    }
    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }
}
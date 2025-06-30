package at.powergrid.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "energy_usage")
// Markiert die Klasse als JPA-Entity - Der Name "energy_usage" wird als Tabellenname verwendet
public class EnergyUsageEntity {

    @Id
    // Primärschlüssel der Tabelle
    @Column(nullable = false,
            columnDefinition = "TIMESTAMP(0) WITHOUT TIME ZONE")
    //  Spalte darf nicht NULL sein
    private LocalDateTime hour; // dient als eindeutiger Schlüssel pro Stunde

    @Column(nullable = false)
    private double communityProduced;

    @Column(nullable = false)
    private double communityUsed;

    @Column(nullable = false)
    private double gridUsed;

    // All-Args-Konstruktor - Erzeugt eine neue Instanz mit allen Feldern, praktisch beim Initialisieren oder in orElseGet()
    public EnergyUsageEntity(LocalDateTime hour, double produced, double used, double grid) {
        this.hour = hour;
        this.communityProduced = produced;
        this.communityUsed = used;
        this.gridUsed = grid;
    }

    // No-Arg-Konstruktor - Für JPA zwingend erforderlich, damit die Entity via Reflection instanziiert werden kann
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

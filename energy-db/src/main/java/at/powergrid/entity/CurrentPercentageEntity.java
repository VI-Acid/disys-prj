package at.powergrid.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
// Markiert die Klasse als JPA-Entity, d. h. sie wird beim Start im Entity-Manager registriert
@Table(name = "current_percentage")
// Legt explizit den Tabellennamen fest. Standard wäre der Klassenname, hier also current_percentage
public class CurrentPercentageEntity {

    @Id
    // Kennzeichnet das Feld als Primärschlüssel. Hier die volle Stunde (LocalDateTime) als eindeutigen Schlüssel
    @Column(nullable = false,
            columnDefinition = "TIMESTAMP(0) WITHOUT TIME ZONE")
    // Spalten-Constraint: In der Datenbank darf dieser Wert nicht NULL sein
    private LocalDateTime hour;

    @Column(nullable = false)
    private double communityDepleted;

    @Column(nullable = false)
    private double gridPortion;

    // No-Arg-Konstruktor - Wird von JPA benötigt, um Objekte via Reflection zu instantiieren
    public CurrentPercentageEntity() {
    }

    // All-Args-Konstruktor - Erleichtert Erstellen neuer Instanzen im Code
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
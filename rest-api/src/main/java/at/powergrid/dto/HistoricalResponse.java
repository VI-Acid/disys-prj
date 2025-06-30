package at.powergrid.dto;

import java.time.OffsetDateTime;

// Respräsentiert die Antwort des REST-API Endpunkts für historische Daten
public class HistoricalResponse {

    private double communityProduced;
    private double communityUsed;
    private double gridUsed;
    private OffsetDateTime timestamp;

    public HistoricalResponse() {
        // Standardkonstruktor für JSON-Deserialisierung
    }

    // Getter & Setter
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
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

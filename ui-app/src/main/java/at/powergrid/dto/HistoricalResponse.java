package at.powergrid.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;


public class HistoricalResponse {

    private double communityProduced;
    private double communityUsed;
    private double gridUsed;
    private OffsetDateTime timestamp;

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }


    // Getter & Setter
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

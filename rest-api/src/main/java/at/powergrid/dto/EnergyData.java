package at.powergrid.dto;

import java.time.OffsetDateTime;

public class EnergyData {
    private OffsetDateTime timestamp;
    private double percentage;
    private double produced_kWh;
    private double used_kWh;
    private double communityDepleted;
    private double gridPortion;


    public EnergyData() {
        // notwendig für Jackson
    }

    public EnergyData(OffsetDateTime timestamp, int hour, double percentage) {
        this.timestamp = timestamp;
        this.percentage = percentage;
    }

    public EnergyData(OffsetDateTime timestamp, double percentage, double produced_kWh, double used_kWh, double gridPortion) {
        this.timestamp = timestamp;
        this.percentage = percentage;
        this.produced_kWh = produced_kWh;
        this.used_kWh = used_kWh;
    }
    public EnergyData(OffsetDateTime timestamp, double communityDepleted, double gridPortion) {
        this.timestamp = timestamp;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }
    public EnergyData(OffsetDateTime timestamp, double percentage, double produced_kWh, double used_kWh, double communityDepleted, double gridPortion) {
        this.timestamp = timestamp;
        this.percentage = percentage;
        this.produced_kWh = produced_kWh;
        this.used_kWh = used_kWh;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }


    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }


    public double getProduced_kWh() {
        return produced_kWh;
    }

    public void setProduced_kWh(double produced_kWh) {
        this.produced_kWh = produced_kWh;
    }

    public double getUsed_kWh() {
        return used_kWh;
    }

    public void setUsed_kWh(double used_kWh) {
        this.used_kWh = used_kWh;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
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

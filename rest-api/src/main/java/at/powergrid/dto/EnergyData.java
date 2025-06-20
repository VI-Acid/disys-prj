package at.powergrid.dto;

import java.time.LocalDateTime;

public class EnergyData {
    private int hour;
    private double percentage;
    private double produced_kWh;
    private double used_kWh;


    public EnergyData(LocalDateTime currentHour, int hour, double percentage) {
        this.hour = hour;
        this.percentage = percentage;
    }

    public EnergyData(int hour, double percentage, double produced_kWh, double used_kWh) {
        this.hour = hour;
        this.percentage = percentage;
        this.produced_kWh = produced_kWh;
        this.used_kWh = used_kWh;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
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

}

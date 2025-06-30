package at.powergrid.dto;

import java.time.OffsetDateTime;

public class EnergyData {
    private OffsetDateTime timestamp;
    private double percentage;
    private double produced_kWh;
    private double used_kWh;

    public EnergyData() {
        // notwendig für Jackson
    }

    /**
     * Timestamp: Stunde (UTC)
     * percentage: hier verwenden wir communityDepleted in %
     * produced_kWh: hier packen wir gridPortion in %
     * used_kWh: unused für current, wird HISTORICAL gefüllt
     */
    public EnergyData(OffsetDateTime timestamp,
                      double percentage,
                      double produced_kWh,
                      double used_kWh) {
        this.timestamp     = timestamp;
        this.percentage    = percentage;
        this.produced_kWh  = produced_kWh;
        this.used_kWh      = used_kWh;
    }

    // --- Getter & Setter ---
    public OffsetDateTime getTimestamp()      { return timestamp; }
    public void setTimestamp(OffsetDateTime t){ this.timestamp = t; }

    public double getPercentage()             { return percentage; }
    public void setPercentage(double p)       { this.percentage = p; }

    public double getProduced_kWh()           { return produced_kWh; }
    public void setProduced_kWh(double p)     { this.produced_kWh = p; }

    public double getUsed_kWh()               { return used_kWh; }
    public void setUsed_kWh(double u)         { this.used_kWh = u; }
}

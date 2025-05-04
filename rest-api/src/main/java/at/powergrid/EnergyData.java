package at.powergrid;

import java.util.UUID;

public class EnergyData {
    private UUID id;
    private int hour;
    private int produced_kWh;
    private int used_kWh;

    public EnergyData(int hour, int produced_kWh, int used_kWh) {
        this.hour = hour;
        this.produced_kWh = produced_kWh;
        this.used_kWh = used_kWh;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int getHour() { return hour; }
    public int getProduced_kWh() { return produced_kWh; }
    public int getUsed_kWh() { return used_kWh; }
}


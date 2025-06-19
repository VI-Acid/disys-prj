package at.powergrid.dto;

import at.powergrid.repository.EnergyUsageEntity;
import at.powergrid.repository.EnergyProductionEntity;
import java.util.List;

public class HistoricalResponse {

    private List<EnergyUsageEntity> usage;
    private List<EnergyProductionEntity> production;

    public HistoricalResponse(List<EnergyUsageEntity> usage, List<EnergyProductionEntity> production) {
        this.usage = usage;
        this.production = production;
    }

    public List<EnergyUsageEntity> getUsage() {
        return usage;
    }

    public List<EnergyProductionEntity> getProduction() {
        return production;
    }

    public void setUsage(List<EnergyUsageEntity> usage) {
        this.usage = usage;
    }

    public void setProduction(List<EnergyProductionEntity> production) {
        this.production = production;
    }
}

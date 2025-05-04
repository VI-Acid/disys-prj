package at.powergrid;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EnergyRepository {
    private final Map<UUID, EnergyData> dataMap = new HashMap<>();

    public static UUID nextId() {
        return UUID.randomUUID();
    }

    public EnergyData save(EnergyData data) {
        return dataMap.put(data.getId(), data);
    }

    public List<EnergyData> findAll() {
        return new ArrayList<>(dataMap.values());
    }

    public void remove(UUID id) {
        dataMap.remove(id);
    }
}
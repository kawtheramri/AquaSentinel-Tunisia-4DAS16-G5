package service;

import entity.BaselineProfile;
import entity.Building;
import entity.ManagerAssignment;
import entity.Meter;
import entity.enums.BuildingType;

import java.time.LocalDate;
import java.util.List;

public interface BuildingService {
    List<Building> getAllBuildings();
    Building getBuildingById(Long id);
    Building getBuildingByCode(String code);
    List<Meter> getMetersByBuilding(Long buildingId);

    Building createBuilding(String code, String name, BuildingType type, String address,
                            String city, Double surfaceM2, Integer occupantsCount, String tariffCategory);
    Building activateBuilding(Long id);
    Building deactivateBuilding(Long id);
    Meter installMeter(Long buildingId, String meterRef, Double initialIndexM3, LocalDate installedAt);
    Meter replaceMeter(Long buildingId, Long oldMeterId, String newMeterRef, Double newInitialIndexM3, LocalDate installedAt);
    ManagerAssignment assignManager(Long buildingId, Long managerId, String reason);
    BaselineProfile computeBaseline(Long buildingId, Double baselineDailyM3, String method);
}
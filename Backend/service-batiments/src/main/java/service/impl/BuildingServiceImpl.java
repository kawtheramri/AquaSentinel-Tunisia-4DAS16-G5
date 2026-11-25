package service.impl;

import entity.BaselineProfile;
import entity.Building;
import entity.ManagerAssignment;
import entity.Meter;
import entity.enums.BuildingStatus;
import entity.enums.BuildingType;
import entity.enums.MeterStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.BaselineProfileRepository;
import repository.BuildingRepository;
import repository.ManagerAssignmentRepository;
import repository.MeterRepository;
import service.BuildingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final MeterRepository meterRepository;
    private final ManagerAssignmentRepository managerAssignmentRepository;
    private final BaselineProfileRepository baselineProfileRepository;

    public BuildingServiceImpl(BuildingRepository buildingRepository,
                               MeterRepository meterRepository,
                               ManagerAssignmentRepository managerAssignmentRepository,
                               BaselineProfileRepository baselineProfileRepository) {
        this.buildingRepository = buildingRepository;
        this.meterRepository = meterRepository;
        this.managerAssignmentRepository = managerAssignmentRepository;
        this.baselineProfileRepository = baselineProfileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Building getBuildingByCode(String code) {
        return buildingRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Building not found with code: " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meter> getMetersByBuilding(Long buildingId) {
        getBuildingById(buildingId);
        return meterRepository.findByBuildingId(buildingId);
    }

    @Override
    public Building createBuilding(String code, String name, BuildingType type, String address,
                                   String city, Double surfaceM2, Integer occupantsCount, String tariffCategory) {
        Building building = new Building();
        building.setCode(code);
        building.setName(name);
        building.setType(type);
        building.setStatus(BuildingStatus.TO_ASSIGN);
        building.setAddress(address);
        building.setCity(city);
        building.setSurfaceM2(surfaceM2);
        building.setOccupantsCount(occupantsCount);
        building.setTariffCategory(tariffCategory);
        return buildingRepository.save(building);
    }

    @Override
    public Building activateBuilding(Long id) {
        Building building = getBuildingById(id);
        building.activate();
        return buildingRepository.save(building);
    }

    @Override
    public Building deactivateBuilding(Long id) {
        Building building = getBuildingById(id);
        building.deactivate();
        return buildingRepository.save(building);
    }

    @Override
    public Meter installMeter(Long buildingId, String meterRef, Double initialIndexM3, LocalDate installedAt) {
        Building building = getBuildingById(buildingId);
        Meter meter = new Meter();
        meter.setMeterRef(meterRef);
        meter.setInitialIndexM3(initialIndexM3);
        meter.setInstalledAt(installedAt != null ? installedAt : LocalDate.now());
        meter.setStatus(MeterStatus.ACTIVE);
        meter.setBuilding(building);
        return meterRepository.save(meter);
    }

    @Override
    public Meter replaceMeter(Long buildingId, Long oldMeterId, String newMeterRef,
                              Double newInitialIndexM3, LocalDate installedAt) {
        Building building = getBuildingById(buildingId);
        Meter oldMeter = meterRepository.findById(oldMeterId)
                .orElseThrow(() -> new RuntimeException("Old meter not found with id: " + oldMeterId));

        Meter newMeter = new Meter();
        newMeter.setMeterRef(newMeterRef);
        newMeter.setInitialIndexM3(newInitialIndexM3);
        newMeter.setInstalledAt(installedAt != null ? installedAt : LocalDate.now());

        building.replaceMeter(oldMeter, newMeter);
        meterRepository.save(oldMeter);
        return meterRepository.save(newMeter);
    }

    @Override
    public ManagerAssignment assignManager(Long buildingId, Long managerId, String reason) {
        Building building = getBuildingById(buildingId);

        managerAssignmentRepository.findFirstByBuildingIdAndUnassignedAtIsNullOrderByAssignedAtDesc(buildingId)
                .ifPresent(current -> current.setUnassignedAt(LocalDateTime.now()));

        ManagerAssignment assignment = new ManagerAssignment();
        assignment.setManagerId(managerId);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setReason(reason);

        building.assignManager(assignment);
        buildingRepository.save(building);
        return managerAssignmentRepository.save(assignment);
    }

    @Override
    public BaselineProfile computeBaseline(Long buildingId, Double baselineDailyM3, String method) {
        Building building = getBuildingById(buildingId);
        BaselineProfile profile = new BaselineProfile();
        profile.setBuilding(building);
        profile.computeBaseline(baselineDailyM3, method);
        return baselineProfileRepository.save(profile);
    }
}
package controller.graphql;

import entity.BaselineProfile;
import entity.Building;
import entity.ManagerAssignment;
import entity.Meter;
import entity.enums.BuildingType;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import service.BuildingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class BuildingGraphQlController {

    private final BuildingService buildingService;

    public BuildingGraphQlController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    // --- Queries ---

    @QueryMapping
    public List<Building> buildings() {
        return buildingService.getAllBuildings();
    }

    @QueryMapping
    public Building buildingById(@Argument Long id) {
        return buildingService.getBuildingById(id);
    }

    @QueryMapping
    public Building buildingByCode(@Argument String code) {
        return buildingService.getBuildingByCode(code);
    }

    @QueryMapping
    public List<Meter> metersByBuilding(@Argument Long buildingId) {
        return buildingService.getMetersByBuilding(buildingId);
    }

    // --- Mutations ---

    @MutationMapping
    public Building createBuilding(@Argument Map<String, Object> input) {
        String code = (String) input.get("code");
        String name = (String) input.get("name");
        BuildingType type = BuildingType.valueOf((String) input.get("type"));
        String address = (String) input.get("address");
        String city = (String) input.get("city");
        Double surfaceM2 = input.get("surfaceM2") != null ? ((Number) input.get("surfaceM2")).doubleValue() : null;
        Integer occupantsCount = input.get("occupantsCount") != null ? ((Number) input.get("occupantsCount")).intValue() : null;
        String tariffCategory = (String) input.get("tariffCategory");

        return buildingService.createBuilding(
                code, name, type, address, city, surfaceM2, occupantsCount, tariffCategory
        );
    }

    @MutationMapping
    public Building activateBuilding(@Argument Long id) {
        return buildingService.activateBuilding(id);
    }

    @MutationMapping
    public Building deactivateBuilding(@Argument Long id) {
        return buildingService.deactivateBuilding(id);
    }

    @MutationMapping
    public Meter installMeter(@Argument Map<String, Object> input) {
        Long buildingId = Long.valueOf(input.get("buildingId").toString());
        String meterRef = (String) input.get("meterRef");
        Double initialIndexM3 = ((Number) input.get("initialIndexM3")).doubleValue();
        LocalDate installedAt = input.get("installedAt") != null
                ? LocalDate.parse(input.get("installedAt").toString())
                : LocalDate.now();

        return buildingService.installMeter(buildingId, meterRef, initialIndexM3, installedAt);
    }

    @MutationMapping
    public Meter replaceMeter(@Argument Long buildingId,
                              @Argument Long oldMeterId,
                              @Argument Map<String, Object> newMeter) {
        String newMeterRef = (String) newMeter.get("meterRef");
        Double newInitialIndexM3 = ((Number) newMeter.get("initialIndexM3")).doubleValue();
        LocalDate installedAt = newMeter.get("installedAt") != null
                ? LocalDate.parse(newMeter.get("installedAt").toString())
                : LocalDate.now();

        return buildingService.replaceMeter(buildingId, oldMeterId, newMeterRef, newInitialIndexM3, installedAt);
    }

    @MutationMapping
    public ManagerAssignment assignManager(@Argument Map<String, Object> input) {
        Long buildingId = Long.valueOf(input.get("buildingId").toString());
        Long managerId = Long.valueOf(input.get("managerId").toString());
        String reason = (String) input.get("reason");

        return buildingService.assignManager(buildingId, managerId, reason);
    }

    @MutationMapping
    public BaselineProfile computeBaseline(@Argument Long buildingId,
                                           @Argument Double baselineDailyM3,
                                           @Argument String method) {
        return buildingService.computeBaseline(buildingId, baselineDailyM3, method);
    }
}
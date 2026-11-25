
package repository;

import entity.MaintenanceRequest;
import entity.enums.MaintenanceStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository
        extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);

    List<MaintenanceRequest> findByBuildingId(Long buildingId);
}

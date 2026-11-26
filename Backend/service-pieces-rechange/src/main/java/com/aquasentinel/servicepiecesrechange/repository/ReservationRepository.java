package com.aquasentinel.servicepiecesrechange.repository;

import com.aquasentinel.servicepiecesrechange.model.Reservation;
import com.aquasentinel.servicepiecesrechange.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findBySparePartIdOrderByReservedAtDesc(Long sparePartId);

    List<Reservation> findByMaintenanceRequestIdOrderByReservedAtDesc(Long maintenanceRequestId);

    List<Reservation> findByStatusOrderByReservedAtDesc(ReservationStatus status);

    Optional<Reservation> findByMaintenanceRequestIdAndSparePartId(Long maintenanceRequestId, Long sparePartId);
}

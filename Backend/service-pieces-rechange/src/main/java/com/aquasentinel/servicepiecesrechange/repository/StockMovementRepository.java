package com.aquasentinel.servicepiecesrechange.repository;

import com.aquasentinel.servicepiecesrechange.model.MovementType;
import com.aquasentinel.servicepiecesrechange.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findBySparePartIdOrderByMovementDateDesc(Long sparePartId);

    List<StockMovement> findByReservationIdOrderByMovementDateDesc(Long reservationId);

    List<StockMovement> findByTypeOrderByMovementDateDesc(MovementType type);
}

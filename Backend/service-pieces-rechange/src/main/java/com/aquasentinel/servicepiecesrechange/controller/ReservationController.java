package com.aquasentinel.servicepiecesrechange.controller;

import com.aquasentinel.servicepiecesrechange.model.Reservation;
import com.aquasentinel.servicepiecesrechange.model.ReservationStatus;
import com.aquasentinel.servicepiecesrechange.model.StockMovement;
import com.aquasentinel.servicepiecesrechange.service.SparePartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Suivi et cycle de vie des réservations de pièces (RESERVED / CONSUMED / RELEASED)")
public class ReservationController {

    private final SparePartService service;

    @Operation(summary = "Lister ou filtrer les réservations par demande de maintenance ou statut")
    @GetMapping
    public List<Reservation> getAll(
            @RequestParam(required = false) Long maintenanceRequestId,
            @RequestParam(required = false) ReservationStatus status
    ) {
        return service.findAllReservations(maintenanceRequestId, status);
    }

    @Operation(summary = "Consulter une réservation par ID")
    @GetMapping("/{id}")
    public Reservation getById(@PathVariable Long id) {
        return service.findReservationById(id);
    }

    @Operation(summary = "Créer une réservation de pièce")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation create(@Valid @RequestBody SparePartController.StockReservationRequest req) {
        return service.reserve(req.partReference(), req.quantity(), req.maintenanceRequestId());
    }

    @Operation(summary = "Libérer une réservation (statut RELEASED)")
    @PatchMapping("/{id}/release")
    public Reservation release(@PathVariable Long id) {
        return service.releaseReservation(id);
    }

    @Operation(summary = "Confirmer l'utilisation / sortie définitive (statut CONSUMED)")
    @PatchMapping("/{id}/confirm-usage")
    public Reservation confirmUsage(@PathVariable Long id) {
        return service.confirmUsage(id);
    }

    @Operation(summary = "Mouvements de stock générés par cette réservation")
    @GetMapping("/{id}/movements")
    public List<StockMovement> movements(@PathVariable Long id) {
        return service.getReservationMovements(id);
    }
}

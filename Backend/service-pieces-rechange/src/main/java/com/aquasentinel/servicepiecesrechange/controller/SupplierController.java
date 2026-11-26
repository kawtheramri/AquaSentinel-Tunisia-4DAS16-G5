package com.aquasentinel.servicepiecesrechange.controller;

import com.aquasentinel.servicepiecesrechange.exception.ConflictException;
import com.aquasentinel.servicepiecesrechange.exception.NotFoundException;
import com.aquasentinel.servicepiecesrechange.model.Supplier;
import com.aquasentinel.servicepiecesrechange.repository.SparePartRepository;
import com.aquasentinel.servicepiecesrechange.repository.SupplierRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "CRUD des fournisseurs de pièces de rechange")
public class SupplierController {

    private final SupplierRepository supplierRepo;
    private final SparePartRepository partRepo;

    @Operation(summary = "Lister tous les fournisseurs")
    @GetMapping
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }

    @Operation(summary = "Consulter un fournisseur par ID")
    @GetMapping("/{id}")
    public Supplier getById(@PathVariable Long id) {
        return supplierRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Fournisseur introuvable : " + id));
    }

    @Operation(summary = "Ajouter un fournisseur")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Supplier create(@Valid @RequestBody Supplier supplier) {
        if (supplierRepo.existsByNameIgnoreCase(supplier.getName())) {
            throw new ConflictException("Fournisseur déjà existant : " + supplier.getName());
        }
        supplier.setId(null);
        return supplierRepo.save(supplier);
    }

    @Operation(summary = "Modifier un fournisseur")
    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @Valid @RequestBody Supplier data) {
        Supplier existing = getById(id);
        if (!existing.getName().equalsIgnoreCase(data.getName()) && supplierRepo.existsByNameIgnoreCase(data.getName())) {
            throw new ConflictException("Fournisseur déjà existant : " + data.getName());
        }
        existing.setName(data.getName());
        existing.setPhone(data.getPhone());
        existing.setEmail(data.getEmail());
        existing.setLeadTimeDays(data.getLeadTimeDays());
        return supplierRepo.save(existing);
    }

    @Operation(summary = "Supprimer un fournisseur")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        getById(id);
        if (partRepo.existsBySupplierId(id)) {
            throw new ConflictException("Impossible de supprimer un fournisseur lié à des pièces");
        }
        supplierRepo.deleteById(id);
    }
}

package com.aquasentinel.servicepiecesrechange.controller;

import com.aquasentinel.servicepiecesrechange.exception.ConflictException;
import com.aquasentinel.servicepiecesrechange.exception.NotFoundException;
import com.aquasentinel.servicepiecesrechange.model.PartCategory;
import com.aquasentinel.servicepiecesrechange.repository.PartCategoryRepository;
import com.aquasentinel.servicepiecesrechange.repository.SparePartRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Part Categories", description = "CRUD des catégories de pièces de rechange")
public class PartCategoryController {

    private final PartCategoryRepository categoryRepo;
    private final SparePartRepository partRepo;

    @Operation(summary = "Lister toutes les catégories")
    @GetMapping
    public List<PartCategory> getAll() {
        return categoryRepo.findAll();
    }

    @Operation(summary = "Consulter une catégorie par ID")
    @GetMapping("/{id}")
    public PartCategory getById(@PathVariable Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable : " + id));
    }

    @Operation(summary = "Créer une catégorie")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartCategory create(@Valid @RequestBody PartCategory category) {
        if (categoryRepo.existsByNameIgnoreCase(category.getName())) {
            throw new ConflictException("Catégorie déjà existante : " + category.getName());
        }
        category.setId(null);
        return categoryRepo.save(category);
    }

    @Operation(summary = "Modifier une catégorie")
    @PutMapping("/{id}")
    public PartCategory update(@PathVariable Long id, @Valid @RequestBody PartCategory data) {
        PartCategory existing = getById(id);
        if (!existing.getName().equalsIgnoreCase(data.getName()) && categoryRepo.existsByNameIgnoreCase(data.getName())) {
            throw new ConflictException("Catégorie déjà existante : " + data.getName());
        }
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        return categoryRepo.save(existing);
    }

    @Operation(summary = "Supprimer une catégorie")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        getById(id);
        if (partRepo.existsByCategoryId(id)) {
            throw new ConflictException("Impossible de supprimer une catégorie liée à des pièces");
        }
        categoryRepo.deleteById(id);
    }
}

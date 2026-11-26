package com.aquasentinel.servicepiecesrechange.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Catégorie de pièces de rechange (entité associée à SparePart). */
@Entity
@Table(name = "part_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;
}

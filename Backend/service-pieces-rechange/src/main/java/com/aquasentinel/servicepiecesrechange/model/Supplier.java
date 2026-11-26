package com.aquasentinel.servicepiecesrechange.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Fournisseur de pièces de rechange (entité associée à SparePart). */
@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(length = 40)
    private String phone;

    @Email(message = "Email invalide")
    @Column(length = 150)
    private String email;

    @Min(value = 0, message = "Le délai de livraison ne peut pas être négatif")
    private Integer leadTimeDays;
}

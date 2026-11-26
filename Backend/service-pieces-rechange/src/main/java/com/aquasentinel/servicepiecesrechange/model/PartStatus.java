package com.aquasentinel.servicepiecesrechange.model;

/** Statut de disponibilité d'une pièce selon le stock disponible et le seuil minimal. */
public enum PartStatus {
    AVAILABLE,
    LOW_STOCK,
    OUT_OF_STOCK
}

package com.example.blog.enums;

/**
 * Stratégie pour la gestion des valeurs null dans MapStruct.
 * Utilisée pour les mappings partiels (PATCH / update).
 */
public enum MapStructNullValueStrategy {
    IGNORE,
    SET_TO_NULL,
    SET_TO_DEFAULT
}

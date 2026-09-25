package com.iwfc.common;

/**
 * Generic contract for domain entities that possess a unique identifier.
 * Demonstrates the use of Java Generics (LO2) to provide type safety across repositories and services.
 *
 * @param <ID> the type of identifier (e.g., String, Long)
 */
public interface Identifiable<ID> {
    /**
     * Returns the unique identifier of the entity.
     *
     * @return the unique ID
     */
    ID getId();
}

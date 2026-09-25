package com.iwfc.repository;

import com.iwfc.common.Identifiable;
import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD repository contract demonstrating Java Generics (LO2).
 *
 * @param <T>  Entity type implementing {@link Identifiable}
 * @param <ID> Key identifier type
 */
public interface Repository<T extends Identifiable<ID>, ID> {

    /**
     * Persists a new entity. Fails if an entity with the same ID already exists.
     *
     * @param entity entity to save
     * @return saved entity
     * @throws DuplicateDataException if entity with the same ID already exists
     */
    T save(T entity) throws DuplicateDataException;

    /**
     * Updates an existing entity. Fails if the entity does not exist.
     *
     * @param entity entity with updated fields
     * @return updated entity
     * @throws EntityNotFoundException if no entity matches the ID
     */
    T update(T entity) throws EntityNotFoundException;

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id entity ID
     * @return Optional containing entity if found, empty otherwise
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities in storage.
     *
     * @return List of all stored entities
     */
    List<T> findAll();

    /**
     * Deletes an entity by its identifier.
     *
     * @param id entity ID
     * @return true if deleted, false if not found
     */
    boolean deleteById(ID id);

    /**
     * Checks if an entity exists by ID.
     *
     * @param id entity ID
     * @return true if exists
     */
    boolean existsById(ID id);

    /**
     * Returns total count of entities.
     *
     * @return count
     */
    long count();
}

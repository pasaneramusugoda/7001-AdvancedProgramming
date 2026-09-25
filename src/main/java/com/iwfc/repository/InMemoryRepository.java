package com.iwfc.repository;

import com.iwfc.common.Identifiable;
import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Thread-safe In-Memory generic repository implementation using Java Collections.
 * Exemplifies Collections framework usage (Map, List) and Generics (LO2).
 *
 * @param <T>  Entity type implementing {@link Identifiable}
 * @param <ID> Key identifier type
 */
public class InMemoryRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {

    private final Map<ID, T> storage = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public T save(T entity) throws DuplicateDataException {
        Objects.requireNonNull(entity, "Entity cannot be null.");
        ID id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null.");
        }

        synchronized (storage) {
            if (storage.containsKey(id)) {
                throw new DuplicateDataException(
                        String.format("Duplicate entry error: Entity with ID '%s' already exists in repository.", id));
            }
            storage.put(id, entity);
            return entity;
        }
    }

    @Override
    public T update(T entity) throws EntityNotFoundException {
        Objects.requireNonNull(entity, "Entity cannot be null.");
        ID id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null.");
        }

        synchronized (storage) {
            if (!storage.containsKey(id)) {
                throw new EntityNotFoundException(
                        String.format("Entity not found error: Entity with ID '%s' does not exist.", id));
            }
            storage.put(id, entity);
            return entity;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        synchronized (storage) {
            return new ArrayList<>(storage.values());
        }
    }

    @Override
    public boolean deleteById(ID id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) return false;
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    /**
     * Clears all repository contents (useful in test lifecycle).
     */
    public void clear() {
        storage.clear();
    }
}

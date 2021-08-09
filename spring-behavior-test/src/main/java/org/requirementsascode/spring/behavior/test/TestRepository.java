package org.requirementsascode.spring.behavior.test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An in memory test double for a Spring CrudRepository.
 * 
 * Original CrudRepository authors: Oliver Gierke, Eberhard Wolff, Jens Schauder
 * 
 * @author b_muth
 *
 * @param <T>  the type of entities to persist
 * @param <ID> the id type
 */
public class TestRepository<T, ID>{
	private static final String THE_GIVEN_ID_MUST_NOT_BE_NULL = "The given id must not be null!";
	private static final String IDS_MUST_NOT_BE_NULL = "Ids must not be null!";
	private static final String ENTITY_MUST_NOT_BE_NULL = "Entity must not be null!";
	private static final String ENTITIES_MUST_NOT_BE_NULL = "Entities must not be null!";

	private final EntityAccess<T, ID> idAccess;
	private final Map<ID, T> entities;

	/**
	 * Creates an instance of a test repository, using the specified id access to
	 * access the id property of the persisted entities.
	 * 
	 * @param entityAccess used for accessing the id and creating copies of the
	 *                     persisted entities
	 */
	public TestRepository(EntityAccess<T, ID> entityAccess) {
		this.idAccess = entityAccess;
		this.entities = new LinkedHashMap<>();
	}

	/**
	 * Saves a given entity. Use the returned instance for further operations as the
	 * save operation might have changed the entity instance completely.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity; will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is
	 *                                  {@literal null}.
	 */
	public T save(T entity) {
		throwsIfNull(entity, ENTITY_MUST_NOT_BE_NULL);

		final ID idToBeSaved = idToBeSavedFor(entity);
		final T savedEntity = saveEntity(entity, idToBeSaved);
		return savedEntity;
	}

	/**
	 * Saves all given entities.
	 *
	 * @param entities must not be {@literal null} nor must it contain
	 *                 {@literal null}.
	 * @return the saved entities; will never be {@literal null}. The returned
	 *         {@literal Iterable} will have the same size as the
	 *         {@literal Iterable} passed as an argument.
	 * @throws IllegalArgumentException in case the given {@link Iterable entities}
	 *                                  or one of its entities is {@literal null}.
	 */
	public Iterable<T> saveAll(Iterable<T> entities) {
		throwsIfNull(entities, ENTITIES_MUST_NOT_BE_NULL);

		entities.forEach(e -> save(e));
		return entities;
	}

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none
	 *         found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	public Optional<T> findById(ID id) {
		throwsIfNull(id, THE_GIVEN_ID_MUST_NOT_BE_NULL);

		T foundEntity = findEntity(id);
		return Optional.ofNullable(foundEntity);
	}

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists,
	 *         {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	public boolean existsById(ID id) {
		return findById(id).isPresent();
	}

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	public Iterable<T> findAll() {
		Set<ID> allIds = entities.keySet();
		Iterable<T> allEntities = findAllById(allIds);
		return allEntities;
	}

	/**
	 * Returns all instances of the type {@code T} with the given IDs.
	 * <p>
	 * If some or all ids are not found, no entities are returned for these IDs.
	 * <p>
	 * Note that the order of elements in the result is not guaranteed.
	 *
	 * @param ids must not be {@literal null} nor contain any {@literal null}
	 *            values.
	 * @return guaranteed to be not {@literal null}. The size can be equal or less
	 *         than the number of given {@literal ids}.
	 * @throws IllegalArgumentException in case the given {@link Iterable ids} or
	 *                                  one of its items is {@literal null}.
	 */
	public Iterable<T> findAllById(Iterable<ID> ids) {
		throwsIfNull(ids, IDS_MUST_NOT_BE_NULL);

		final List<T> entitiesWithAnyId = StreamSupport.stream(ids.spliterator(), false).map(this::findById)
			.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

		return entitiesWithAnyId;
	}

	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities.
	 */
	public long count() {
		return entities.size();
	}

	/**
	 * Deletes the entity with the given id.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal id} is
	 *                                  {@literal null}
	 */
	public void deleteById(ID id) {
		throwsIfNull(id, THE_GIVEN_ID_MUST_NOT_BE_NULL);
		entities.remove(id);
	}

	/**
	 * Deletes a given entity.
	 *
	 * @param entity must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	public void delete(T entity) {
		throwsIfNull(entity, ENTITY_MUST_NOT_BE_NULL);

		ID entityId = idAccess.idOf(entity);
		deleteById(entityId);
	}

	/**
	 * Deletes all instances of the type {@code T} with the given IDs.
	 *
	 * @param ids must not be {@literal null}. Must not contain {@literal null}
	 *            elements.
	 * @throws IllegalArgumentException in case the given {@literal ids} or one of
	 *                                  its elements is {@literal null}.
	 * @since 2.5
	 */
	public void deleteAllById(Iterable<ID> ids) {
		throwsIfNull(ids, IDS_MUST_NOT_BE_NULL);

		for (ID id : ids) {
			deleteById(id);
		}
	}

	/**
	 * Deletes the given entities.
	 *
	 * @param entities must not be {@literal null}. Must not contain {@literal null}
	 *                 elements.
	 * @throws IllegalArgumentException in case the given {@literal entities} or one
	 *                                  of its entities is {@literal null}.
	 */
	public void deleteAll(Iterable<T> entities) {
		throwsIfNull(entities, ENTITIES_MUST_NOT_BE_NULL);

		List<ID> ids = StreamSupport.stream(entities.spliterator(), false).map(idAccess::idOf).collect(Collectors.toList());

		deleteAllById(ids);
	}

	/**
	 * Deletes all entities managed by the repository.
	 */
	public void deleteAll() {
		final Iterable<T> allEntities = findAll();
		deleteAll(allEntities);
	}

	private T findEntity(ID id) {
		final T entity = entities.get(id);
		return copyOf(entity);
	}

	private ID idToBeSavedFor(T entity) {
		ID existingEntityId = idAccess.idOf(entity);
		final ID idToBeSaved = existingEntityId == null ? idAccess.nextId() : existingEntityId;
		return idToBeSaved;
	}

	T saveEntity(T entity, final ID idToBeSaved) {
		T entityToBeSaved = idAccess.copyWithId(entity, idToBeSaved);
		entities.put(idToBeSaved, entityToBeSaved);
		final T clonedEntity = copyOf(entityToBeSaved);
		return clonedEntity;
	}

	private T copyOf(final T entity) {
		T entityClone = entity != null ? idAccess.copyWithId(entity, idAccess.idOf(entity)) : null;
		return entityClone;
	}

	private void throwsIfNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
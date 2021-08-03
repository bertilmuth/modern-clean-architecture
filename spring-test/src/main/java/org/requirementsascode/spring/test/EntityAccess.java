package org.requirementsascode.spring.test;

/**
 * Provides the id of an existing entity, a new id for an entity to be created,
 * or creates a new entity with a certain id.
 * 
 * @author b_muth
 *
 * @param <T>  the type of entity
 * @param <ID> the id type
 */
public interface EntityAccess<T, ID> {
	/**
	 * Returns the id of the specified entity.
	 * 
	 * @param entity the entity to be asked for its id
	 * @return the entity id
	 */
	ID idOf(T entity);

	/**
	 * Provides a new id, for an entity to be created. For example, this could be a
	 * random UUID or it could increment a counter for Long ids.
	 * 
	 * @return the newly created id
	 */
	ID nextId();

	/**
	 * Copes an existing entity, and assigns it the specified id. The newly created
	 * entity isn't allowed to share state with the specified entity.
	 * 
	 * @param entity the entity to be copied
	 * @param id     the id of the new entity
	 * 
	 * @return the new entity, with the id
	 */
	T copyWithId(T entity, ID id);
}

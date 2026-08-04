package dev.forgepack.core.api.mapper;

import java.util.Set;

/**
 * Contract for mapping between domain entities and Data Transfer Objects (DTOs).
 *
 * <p>This interface defines a standardized way to convert between
 * request/response DTOs and domain entities, promoting separation of concerns
 * between the API layer and the persistence layer.</p>
 *
 * <p>Implementations are responsible for handling field mapping, type conversion,
 * and any transformation logic required between layers.</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *     <li>Convert request DTOs into domain entities</li>
 *     <li>Convert domain entities into response DTOs</li>
 *     <li>Apply partial or full updates to existing entities</li>
 * </ul>
 *
 * <p>This abstraction is typically used in the service layer to decouple
 * controllers from persistence models.</p>
 *
 * @param <Entity> domain entity type
 * @param <Request> request DTO type
 * @param <Response> response DTO type
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
public interface Mapper<Entity, Request, Response> {

    /**
     * Converts a request DTO into a new domain entity instance.
     *
     * <p>This method is typically used during create operations.</p>
     *
     * @param dto request DTO containing input data
     * @return a new entity instance populated with the provided data
     */
    public Entity toEntity(Request dto);

    /**
     * Converts a domain entity into a response DTO.
     *
     * <p>This method is used to expose entity data in a controlled and
     * client-friendly format.</p>
     *
     * @param entity domain entity to be converted
     * @return response DTO representation of the entity
     */
    public Response toResponse(Entity entity);

    /**
     * Updates an existing entity instance using data from a request DTO.
     *
     * <p>This method is typically used in update operations to apply changes
     * without creating a new entity instance.</p>
     *
     * <p>Implementations may choose to perform partial updates (ignoring null
     * fields) or full updates, depending on the desired behavior.</p>
     *
     * @param dto request DTO containing updated data
     * @param entity existing entity instance to be modified
     */
    public void updateEntity(Request dto, Entity entity);

    /**
     * Converts a set of entities into a set of response DTOs.
     *
     * @param entities set of entities to convert
     * @return set of response DTOs, or an empty set if {@code entities} is {@code null}
     */
    public Set<Response> toResponseSet(Set<Entity> entities);
}

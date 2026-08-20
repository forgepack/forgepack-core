package dev.forgepack.core.api.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.repository.RepositoryCrud;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;

/**
 * Defines the contract for application services responsible for managing
 * domain entities and their corresponding DTO representations.
 *
 * <p>This interface declares the standard CRUD operations expected from
 * service layer components. Implementations are responsible for handling
 * business logic, coordinating persistence operations, and converting
 * between entities and Data Transfer Objects (DTOs).</p>
 *
 * <p>The service operates with three generic types:</p>
 * <ul>
 *     <li>The domain entity</li>
 *     <li>The request DTO used for create and update operations</li>
 *     <li>The response DTO returned by service methods</li>
 * </ul>
 *
 * <p>Implementations may also enrich responses with additional metadata,
 * such as HATEOAS links.</p>
 *
 * @param <Entity> domain entity type
 * @param <DTORequest> DTO used for create and update operations
 * @param <DTOResponse> DTO returned in service responses
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see Mapper
 * @see RepositoryCrud
 */
public interface ServiceCrudMutable<Entity, DTORequest, DTOResponse> extends ServiceCrudRead<Entity, DTOResponse> {

    /**
     * Creates and persists a new entity based on the provided {@link DTORequest}.
     *
     * <p>The {@link DTORequest} is converted into a domain entity using the configured
     * {@link Mapper}. The entity is then persisted through the
     * {@link RepositoryCrud}. After persistence, the entity is converted
     * back into a {@link DTOResponse} enriched with HATEOAS links.</p>
     *
     * @param created {@link DTORequest} containing the data required to create the entity
     * @return {@link DTOResponse} representing the persisted entity with HATEOAS links
     */
    DTOResponse create(DTORequest created);

    /**
     * Updates an existing resource identified by the given ID.
     *
     * <p>The method first verifies if an entity with the specified identifier
     * exists. If the entity does not exist, an {@link EntityNotFoundException}
     * is thrown.</p>
     *
     * <p>The DTO is converted to an entity using the configured {@link Mapper},
     * persisted, and then returned as a {@link DTOResponse} enriched with HATEOAS links.</p>
     *
     * @param id      unique identifier of the resource to update
     * @param updated {@link DTORequest} containing updated entity data
     * @return {@link DTOResponse} representing the updated entity
     * @throws EntityNotFoundException if the entity does not exist
     */
    DTOResponse update(UUID id, DTORequest updated);
}

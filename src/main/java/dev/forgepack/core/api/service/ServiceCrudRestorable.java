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
public interface ServiceCrudRestorable<Entity, DTORequest, DTOResponse> extends ServiceCrudMutable<Entity, DTORequest, DTOResponse> {

    /**
     * Soft deletes an entity identified by the specified identifier.
     *
     * <p>If the entity does not exist, an {@link EntityNotFoundException}
     * is thrown.</p>
     *
     * @param id {@link UUID} unique identifier of the entity to be soft deleted
     * @return {@link DTOResponse} representing the soft deleted entity with HATEOAS links
     * @throws EntityNotFoundException if the entity does not exist
     */
    DTOResponse softDelete(UUID id);

    /**
     * Restores an entity identified by the specified identifier.
     *
     * <p>If the entity does not exist, an {@link EntityNotFoundException}
     * is thrown.</p>
     *
     * @param id {@link UUID} unique identifier of the entity to be soft restored
     * @return {@link DTOResponse} representing the soft restored entity with HATEOAS links
     * @throws EntityNotFoundException if the entity does not exist
     */
    DTOResponse restore(UUID id);

    /**
     * Hard deletes an entity identified by the specified identifier.
     *
     * <p>If the entity does not exist, an {@link EntityNotFoundException}
     * is thrown.</p>
     *
     * @param id {@link UUID} unique identifier of the entity to be hard deleted
     * @return {@link DTOResponse} representing the hard deleted entity with HATEOAS links
     * @throws EntityNotFoundException if the entity does not exist
     */
    DTOResponse hardDelete(UUID id);
}

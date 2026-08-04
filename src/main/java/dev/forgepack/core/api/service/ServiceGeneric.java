package dev.forgepack.core.api.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.repository.RepositoryGeneric;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * @see RepositoryGeneric
 */
public interface ServiceGeneric<Entity, DTORequest, DTOResponse> {

    /**
     * Creates and persists a new entity based on the provided {@link DTORequest}.
     *
     * <p>The {@link DTORequest} is converted into a domain entity using the configured
     * {@link Mapper}. The entity is then persisted through the
     * {@link RepositoryGeneric}. After persistence, the entity is converted
     * back into a {@link DTOResponse} enriched with HATEOAS links.</p>
     *
     * @param created {@link DTORequest} containing the data required to create the entity
     * @return {@link DTOResponse} representing the persisted entity with HATEOAS links
     */
    DTOResponse create(DTORequest created);

    /**
     * Retrieves entities using pagination and dynamic filtering.
     *
     * <p>The filtering behavior is determined by the property specified in the
     * {@link Pageable} sort configuration. If the sorted property is {@code id}
     * and the provided value is a valid {@link UUID}, the search will be performed
     * directly by identifier.</p>
     *
     * <p>Otherwise, the method dynamically builds a query using Spring Data
     * {@link Example} and {@link ExampleMatcher}, performing case-insensitive
     * and partial string matching.</p>
     *
     * <p>If the property cannot be resolved or an error occurs during query
     * construction, the method falls back to returning all entities using the
     * provided pagination.</p>
     *
     * @param pageable pagination and sorting configuration
     * @param value value used as search filter
     * @param entityClass class of the entity being queried
     * @return a paginated list of response DTOs with HATEOAS links
     */
    Page<DTOResponse> findAll(Pageable pageable, String value, Class<Entity> entityClass);

    /**
     * Retrieves a single entity by its unique identifier.
     *
     * <p>If no entity exists with the specified identifier, an
     * {@link EntityNotFoundException} is thrown.</p>
     *
     * @param id {@link UUID} unique identifier of the entity
     * @return {@link DTOResponse} representing the found entity with HATEOAS links
     * @throws EntityNotFoundException if the entity does not exist
     */
    DTOResponse findById(UUID id);

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

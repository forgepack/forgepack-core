package dev.forgepack.core.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for ForgePack data access layer.
 *
 * <p>This interface extends {@link JpaRepository} to provide a common
 * abstraction for all repositories in the library, standardizing the use
 * of {@link UUID} as the entity identifier type.</p>
 *
 * <p>The {@link NoRepositoryBean} annotation indicates that this interface
 * is not intended to be instantiated directly, but rather to be extended
 * by concrete repository interfaces.</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *     <li>Provide standard CRUD operations via {@link JpaRepository}</li>
 *     <li>Enforce UUID as the identifier type</li>
 *     <li>Offer additional convenience methods for paginated queries by ID</li>
 * </ul>
 *
 * <p>Custom repositories should extend this interface to inherit common
 * behavior and maintain consistency across the data access layer.</p>
 *
 * @param <T> entity type managed by the repository
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see JpaRepository
 * @see NoRepositoryBean
 */
@NoRepositoryBean
public interface RepositoryGeneric<T> extends JpaRepository<T, UUID> {

    /**
     * Retrieves a paginated result containing entities that match the given ID.
     *
     * <p>This method is useful when combining ID filtering with pagination,
     * although typically only one result is expected due to ID uniqueness.</p>
     *
     * @param uuid identifier to filter by
     * @param pageable pagination and sorting information
     * @return page containing matching entities
     */
    Page<T> findById(UUID uuid, Pageable pageable);

    /**
     * Retrieves a non-deleted entity by its unique identifier.
     *
     * <p>This method is typically used to fetch active (non-soft-deleted) entities,
     * excluding those with a non-null {@code deletedAt} timestamp.</p>
     *
     * @param id identifier of the entity to retrieve
     * @return an {@link Optional} containing the matching entity, or empty if
     *         not found or already soft-deleted
     */
    Optional<T> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Retrieves a paginated result filtered by ID and ordered by ID ascending.
     *
     * <p>Although ordering is redundant when filtering by a unique identifier,
     * this method ensures deterministic ordering when used in broader queries
     * or extended implementations.</p>
     *
     * @param id identifier to filter by
     * @param pageable pagination and sorting information
     * @return page containing matching entities ordered by ID
     */
    Page<T> findByIdOrderByIdAsc(UUID id, Pageable pageable);
}

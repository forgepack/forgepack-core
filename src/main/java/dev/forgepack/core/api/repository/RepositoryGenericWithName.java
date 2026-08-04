package dev.forgepack.core.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.Optional;
import java.util.UUID;

/**
 * Specialized repository contract for entities that expose a {@code name} attribute.
 *
 * <p>This interface extends {@link RepositoryGeneric} by adding query methods
 * based on a conventional {@code name} field, enabling common operations such as
 * search, filtering, and uniqueness validation.</p>
 *
 * <p>All methods rely on Spring Data JPA query derivation and assume that the
 * underlying entity contains a {@code name} property mapped to a persistent field.</p>
 *
 * <h3>Capabilities</h3>
 * <ul>
 *     <li>Search by exact name</li>
 *     <li>Case-insensitive existence checks</li>
 *     <li>Uniqueness validation excluding a specific entity (useful for updates)</li>
 *     <li>Paginated and ordered search using partial name matching</li>
 * </ul>
 *
 * <p>This interface is typically used in conjunction with validation logic
 * (e.g. uniqueness constraints) and generic service layers.</p>
 *
 * @param <T> entity type that contains a {@code name} attribute
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see RepositoryGeneric
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@NoRepositoryBean
public interface RepositoryGenericWithName<T> extends RepositoryGeneric<T> {

    /**
     * Retrieves the entity with the exact given name, if present.
     *
     * @param name name to search for
     * @return an {@link Optional} containing the matching entity, or empty if none found
     */
    Optional<T> findByName(String name);

    /**
     * Checks if any entity exists with the given name.
     *
     * @param name name to check
     * @return {@code true} if at least one entity exists with the given name,
     *         {@code false} otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if any entity exists with the given name, ignoring case.
     *
     * @param name name to check
     * @return {@code true} if a matching entity exists (case-insensitive),
     *         {@code false} otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Checks if any entity exists with the given name (case-insensitive),
     * excluding the entity identified by the given ID.
     *
     * <p>This method is typically used during update operations to ensure
     * uniqueness while ignoring the current entity.</p>
     *
     * @param name name to check
     * @param id identifier of the entity to exclude from the check
     * @return {@code true} if another entity exists with the same name,
     *         {@code false} otherwise
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    /**
     * Retrieves a paginated list of entities whose names contain the given value,
     * ignoring case and ordered alphabetically by name.
     *
     * @param pageable pagination and sorting information
     * @param name partial name used as filter
     * @return page of entities matching the filter criteria
     */
    Page<T> findByNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);
}

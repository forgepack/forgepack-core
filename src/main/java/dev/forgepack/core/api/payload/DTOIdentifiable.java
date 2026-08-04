package dev.forgepack.core.api.payload;

import java.util.UUID;

/**
 * Contract for request DTOs that carry an entity identifier.
 *
 * <p>This interface defines a common abstraction for request payloads that
 * reference an existing entity through a {@link UUID} identifier. It enables
 * generic handling of operations that depend on entity identification,
 * such as updates and validation logic.</p>
 *
 * <p>Implementations are typically used in service and validation layers
 * to access the identifier in a uniform and type-safe manner.</p>
 *
 * <h3>Usage scenarios</h3>
 * <ul>
 *     <li>Update operations where the target entity must be identified</li>
 *     <li>Uniqueness validation excluding the current entity instance</li>
 *     <li>Generic service processing for identifiable request DTOs</li>
 * </ul>
 *
 * <h3>Identifier semantics</h3>
 * <ul>
 *     <li>The {@code id} may be {@code null} for create operations</li>
 *     <li>The {@code id} must be present for update operations</li>
 * </ul>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see UUID
 */
public interface DTOIdentifiable<T> {

    /**
     * Returns the unique identifier of the target entity.
     *
     * @return entity identifier, or {@code null} if the request or response represents
     *         a creation operation
     */
    T id();
}

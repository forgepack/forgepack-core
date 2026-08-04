package dev.forgepack.core.api.controller;

import org.springframework.http.ResponseEntity;
import java.util.UUID;

/**
 * Contract for REST controllers that support advanced lifecycle operations
 * such as hard deletion and restoration of resources.
 *
 * <p>This interface is intended to be used as an optional extension to
 * {@link ControllerGeneric}, providing additional endpoints for managing
 * resource states beyond standard CRUD operations.</p>
 *
 * <p>These operations are typically applicable in systems that implement
 * soft delete strategies, where resources are logically removed but can
 * still be restored or permanently deleted.</p>
 *
 * <h3>Supported operations</h3>
 * <ul>
 *     <li>Permanently delete a resource (hard delete)</li>
 *     <li>Restore a previously deleted resource</li>
 * </ul>
 *
 * <h3>Usage considerations</h3>
 * <ul>
 *     <li>These operations may have security restrictions and should be protected accordingly</li>
 *     <li>Not all resources should expose these endpoints publicly</li>
 *     <li>Implementations should ensure consistency with the underlying data model</li>
 * </ul>
 *
 * @param <DTOResponse> type representing the response payload
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
public interface ControllerLifecycle<DTOResponse> {

    /**
     * Permanently deletes a resource, removing it from the data store.
     *
     * <p>This operation bypasses any soft delete mechanism and results in
     * irreversible data removal.</p>
     *
     * @param id identifier of the resource to be permanently deleted
     * @return {@link ResponseEntity} with no content, indicating the resource
     * was permanently deleted (HTTP 204)
     */
    ResponseEntity<Void> hardDelete(UUID id);

    /**
     * Restores a previously deleted resource.
     *
     * <p>This operation is applicable only to resources that implement a
     * soft delete mechanism. The resource is made active again and becomes
     * accessible through standard retrieval operations.</p>
     *
     * @param id identifier of the resource to be restored
     * @return {@link ResponseEntity} containing the restored resource
     */
    ResponseEntity<DTOResponse> restore(UUID id);
}

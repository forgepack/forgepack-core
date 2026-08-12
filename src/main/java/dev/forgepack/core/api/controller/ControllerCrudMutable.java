package dev.forgepack.core.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Generic contract for REST controllers providing standard CRUD operations.
 *
 * <p>This interface defines a reusable abstraction for REST endpoints,
 * supporting creation, retrieval (by ID and paginated), update, and deletion
 * of resources.</p>
 *
 * <p>Implementations are expected to follow RESTful conventions and return
 * {@link ResponseEntity} wrappers to allow full control over HTTP responses.</p>
 *
 * <h3>Supported operations</h3>
 * <ul>
 *     <li>Create a new resource</li>
 *     <li>Update an existing resource</li>
 * </ul>
 *
 * <h3>Validation</h3>
 * <p>Request payloads are validated using {@code @Valid} and Bean Validation.</p>
 *
 * @param <DTORequest> type representing the request payload
 * @param <DTOResponse> type representing the response payload
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
public interface ControllerCrudMutable<DTORequest, DTOResponse> {

    /**
     * Creates a new resource.
     *
     * @param created request payload containing resource data
     * @return {@link ResponseEntity} containing the created resource
     */
    ResponseEntity<DTOResponse> create(@RequestBody @Valid DTORequest created);

    /**
     * Updates an existing resource.
     *
     * @param id identifier of the resource to update
     * @param updated request payload containing updated data
     * @return {@link ResponseEntity} containing the updated resource
     */
    ResponseEntity<DTOResponse> update(UUID id, @RequestBody @Valid DTORequest updated);
}

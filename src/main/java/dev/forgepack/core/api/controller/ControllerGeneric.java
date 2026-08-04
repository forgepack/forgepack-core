package dev.forgepack.core.api.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
 *     <li>Retrieve resources with pagination and optional filtering</li>
 *     <li>Retrieve a resource by its unique identifier</li>
 *     <li>Update an existing resource</li>
 *     <li>Delete a resource</li>
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
public interface ControllerGeneric<DTORequest, DTOResponse> {

    /**
     * Creates a new resource.
     *
     * @param created request payload containing resource data
     * @return {@link ResponseEntity} containing the created resource
     */
    ResponseEntity<DTOResponse> create(@RequestBody @Valid DTORequest created);

    /**
     * Retrieves a paginated list of resources, optionally filtered by a value.
     *
     * <p>The {@code value} parameter may be used as a search term or filter,
     * depending on the implementation.</p>
     *
     * @param value optional filter value
     * @param pageable pagination and sorting information
     * @return {@link ResponseEntity} containing a page of resources
     */
    ResponseEntity<Page<DTOResponse>> findAll(@RequestParam(name = "value", defaultValue = "", required = false) String value, Pageable pageable);

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param id unique identifier of the resource
     * @return {@link ResponseEntity} containing the requested resource
     */
    ResponseEntity<DTOResponse> findById(UUID id);

    /**
     * Updates an existing resource.
     *
     * @param id identifier of the resource to update
     * @param updated request payload containing updated data
     * @return {@link ResponseEntity} containing the updated resource
     */
    ResponseEntity<DTOResponse> update(UUID id, @RequestBody @Valid DTORequest updated);

    /**
     * Deletes a resource by its unique identifier.
     *
     * @param id identifier of the resource to delete
     * @return {@link ResponseEntity} with no content, indicating the resource
     * was soft deleted (HTTP 204)
     */
    ResponseEntity<Void> delete(UUID id);
}

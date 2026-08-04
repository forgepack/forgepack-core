package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.controller.ControllerGeneric;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceGeneric;
import dev.forgepack.core.internal.model.GenericAuditEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

public abstract class ControllerGenericImpl<Entity extends GenericAuditEntity, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends DTOIdentifiable<UUID>> implements ControllerGeneric<DTORequest, DTOResponse> {

    private final Class<Entity> entityClass;
    private final ServiceGeneric<Entity, DTORequest, DTOResponse> serviceGeneric;

    public ControllerGenericImpl(Class<Entity> entityClass, ServiceGeneric<Entity, DTORequest, DTOResponse> serviceGeneric) {
        this.entityClass = entityClass;
        this.serviceGeneric = serviceGeneric;
    }
//    @PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('user:create')")
    @PostMapping("")
    @Override
    public ResponseEntity<DTOResponse> create(@Valid DTORequest created){
        DTOResponse body = serviceGeneric.create(created);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(body.id())
                .toUri();
        return ResponseEntity.created(uri).body(body);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER') and hasAnyAuthority('user:retrieve')")
    @GetMapping("")
    @Override
    public ResponseEntity<Page<DTOResponse>> findAll(@RequestParam String value, Pageable pageable){
        return ResponseEntity.ok().body(serviceGeneric.findAll(pageable, value, entityClass));
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER') and hasAnyAuthority('user:retrieve')")
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<DTOResponse> findById(@PathVariable UUID id){
        return ResponseEntity.ok().body(serviceGeneric.findById(id));
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') and hasAnyAuthority('user:update')")
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<DTOResponse> update(@PathVariable UUID id, @Valid DTORequest updated){
        return ResponseEntity.ok().body(serviceGeneric.update(id, updated));
    }

//    @PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('user:delete')")
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        serviceGeneric.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}

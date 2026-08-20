package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.controller.ControllerCrudMutable;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceCrudMutable;
import dev.forgepack.core.internal.model.EntityCrud;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

public abstract class ControllerCrudMutableImpl<Entity extends EntityCrud, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends DTOIdentifiable<UUID>>
        implements ControllerCrudMutable<DTORequest, DTOResponse> {

    private final Class<Entity> entityClass;
    private final ServiceCrudMutable<Entity, DTORequest, DTOResponse> serviceCrudMutable;

    public ControllerCrudMutableImpl(Class<Entity> entityClass, ServiceCrudMutable<Entity, DTORequest, DTOResponse> serviceCrudMutable) {
        this.entityClass = entityClass;
        this.serviceCrudMutable = serviceCrudMutable;
    }
//    @PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('user:create')")
    @PostMapping("")
    @Override
    public ResponseEntity<DTOResponse> create(@Valid DTORequest created){
        DTOResponse body = serviceCrudMutable.create(created);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(body.id())
                .toUri();
        return ResponseEntity.created(uri).body(body);
    }
//    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') and hasAnyAuthority('user:update')")
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<DTOResponse> update(@PathVariable UUID id, @Valid DTORequest updated){
        return ResponseEntity.ok().body(serviceCrudMutable.update(id, updated));
    }
}

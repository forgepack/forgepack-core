package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.controller.ControllerCrudRead;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceCrudRead;
import dev.forgepack.core.internal.model.EntityCrud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

public abstract class ControllerCrudReadImpl<Entity extends EntityCrud, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends DTOIdentifiable<UUID>>
        implements ControllerCrudRead<DTOResponse> {

    private final Class<Entity> entityClass;
    private final ServiceCrudRead<Entity, DTOResponse> serviceCrudRead;

    public ControllerCrudReadImpl(Class<Entity> entityClass, ServiceCrudRead<Entity, DTOResponse> serviceCrudRead) {
        this.entityClass = entityClass;
        this.serviceCrudRead = serviceCrudRead;
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER') and hasAnyAuthority('user:retrieve')")
    @GetMapping("")
    @Override
    public ResponseEntity<Page<DTOResponse>> findAll(@RequestParam String value, Pageable pageable){
        return ResponseEntity.ok().body(serviceCrudRead.findAll(pageable, value, entityClass));
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER') and hasAnyAuthority('user:retrieve')")
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<DTOResponse> findById(@PathVariable UUID id){
        return ResponseEntity.ok().body(serviceCrudRead.findById(id));
    }
}

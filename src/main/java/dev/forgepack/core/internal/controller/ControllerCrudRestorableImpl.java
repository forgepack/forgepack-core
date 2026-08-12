package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.controller.ControllerCrudRestorable;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceCrudRestorable;
import dev.forgepack.core.internal.model.GenericAuditEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.UUID;

public abstract class ControllerCrudRestorableImpl<Entity extends GenericAuditEntity, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends DTOIdentifiable<UUID>>
    implements ControllerCrudRestorable<DTOResponse> {

    private final Class<Entity> entityClass;
    private final ServiceCrudRestorable<Entity, DTOResponse> ServiceCrudRestorable;

    public ControllerCrudRestorableImpl(Class<Entity> entityClass, ServiceCrudRestorable<Entity, DTOResponse> serviceCrudRestorable) {
        this.entityClass = entityClass;
        this.ServiceCrudRestorable = serviceCrudRestorable;
    }
//    @PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('user:delete')")
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> hardDelete(@PathVariable UUID id){
        ServiceCrudRestorable.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
//    @PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('user:delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<DTOResponse> restore(@PathVariable UUID id){
        return ResponseEntity.accepted().body(ServiceCrudRestorable.restore(id));
    }
}

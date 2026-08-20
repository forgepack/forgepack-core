package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.controller.ControllerCrudRestorable;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceCrudRestorable;
import dev.forgepack.core.internal.model.EntityCrud;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.UUID;

public abstract class ControllerCrudRestorableImpl<Entity extends EntityCrud, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends DTOIdentifiable<UUID>>
    extends ControllerCrudMutableImpl<Entity, DTORequest, DTOResponse>
    implements ControllerCrudRestorable<DTORequest, DTOResponse> {

    private final Class<Entity> entityClass;
    private final ServiceCrudRestorable<Entity, DTORequest, DTOResponse> ServiceCrudRestorable;

    public ControllerCrudRestorableImpl(Class<Entity> entityClass, ServiceCrudRestorable<Entity, DTORequest, DTOResponse> serviceCrudRestorable) {
        super(entityClass, serviceCrudRestorable);
        this.entityClass = entityClass;
        this.ServiceCrudRestorable = serviceCrudRestorable;
    }
//    @PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('user:delete')")
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> softDelete(@PathVariable UUID id){
        ServiceCrudRestorable.softDelete(id);
        return ResponseEntity.noContent().build();
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

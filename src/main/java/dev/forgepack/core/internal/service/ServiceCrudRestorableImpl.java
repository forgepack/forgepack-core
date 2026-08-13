package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.api.service.ServiceCrudMutable;
import dev.forgepack.core.api.service.ServiceCrudRestorable;
import dev.forgepack.core.internal.model.GenericAuditEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ServiceCrudMutable}.
 *
 * <p>Delegates persistence to a {@link RepositoryCrud} and conversion to a
 * {@link Mapper}. Enriches response DTOs with HATEOAS self links via
 * {@link #addHateoas(GenericAuditEntity)}.</p>
 *
 * @param <Entity> domain entity type extending {@link GenericAuditEntity}
 * @param <DTORequest> request DTO extending {@link DTOIdentifiable}, used for create and update operations
 * @param <DTOResponse> response DTO extending {@link RepresentationModel}, returned by service operations
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see ServiceCrudMutable
 * @see RepositoryCrud
 * @see Mapper
 */
public abstract class ServiceCrudRestorableImpl<Entity extends GenericAuditEntity, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends RepresentationModel<DTOResponse>>
    extends ServiceUtils<Entity, DTORequest, DTOResponse>
    implements ServiceCrudRestorable<Entity, DTOResponse> {

    private final Class<Entity> entity;
    private final RepositoryCrud<Entity> repositoryGeneric;
    private final Mapper<Entity, DTORequest, DTOResponse> mapper;
    private static final Logger log = LoggerFactory.getLogger(ServiceCrudRestorableImpl.class);

    public ServiceCrudRestorableImpl(Class<Entity> entity, RepositoryCrud<Entity> repositoryGeneric, Mapper<Entity, DTORequest, DTOResponse> mapper) {
        super(entity, repositoryGeneric, mapper);
        this.entity = entity;
        this.repositoryGeneric = repositoryGeneric;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DTOResponse softDelete(UUID id){
        Entity entity = existsEntity("soft delete", id);
        entity.setDeletedAt(LocalDateTime.now());
        repositoryGeneric.save(entity);
        addLog("soft delete", id, null, null);
        return addHateoas(entity);
    }

    @Override
    @Transactional
    public DTOResponse restore(UUID id){
        Entity entity = existsEntity("restore", id);
        entity.setDeletedAt(null);
        repositoryGeneric.save(entity);
        addLog("restore", id, null, null);
        return addHateoas(entity);
    }

    @Override
    @Transactional
    public DTOResponse hardDelete(UUID id){
        Entity entity = existsEntity("hard delete", id);
        repositoryGeneric.delete(entity);
        addLog("hard delete", id, null, null);
        return addHateoas(entity);
    }
}

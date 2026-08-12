package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.api.service.ServiceCrudMutable;
import dev.forgepack.core.internal.model.GenericAuditEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.hateoas.RepresentationModel;
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
public abstract class ServiceCrudMutableImpl<Entity extends GenericAuditEntity, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends RepresentationModel<DTOResponse>>
    implements ServiceCrudMutable<Entity, DTORequest, DTOResponse> {

    private final Class<Entity> entity;
    private final RepositoryCrud<Entity> repositoryGeneric;
    private final Mapper<Entity, DTORequest, DTOResponse> mapper;
    private final ServiceUtils<Entity, DTORequest, DTOResponse> serviceUtils;
    private static final Logger log = LoggerFactory.getLogger(ServiceCrudMutableImpl.class);

    public ServiceCrudMutableImpl(Class<Entity> entity, RepositoryCrud<Entity> repositoryGeneric, Mapper<Entity, DTORequest, DTOResponse> mapper) {
        this.entity = entity;
        this.serviceUtils = new ServiceUtils<>(entity, repositoryGeneric, mapper) {};
        this.repositoryGeneric = repositoryGeneric;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DTOResponse create(DTORequest created){
        Entity entity = repositoryGeneric.save(mapper.toEntity(created));
        serviceUtils.addLog("create", entity.getId(), null, null);
        return serviceUtils.addHateoas(entity);
    }

    @Override
    @Transactional
    public DTOResponse update(UUID id, DTORequest updated){
        Entity entity = serviceUtils.existsEntity("update", id);
        mapper.updateEntity(updated, entity);
        Entity ratified = repositoryGeneric.save(entity);
        serviceUtils.addLog("update", id, null, null);
        return serviceUtils.addHateoas(ratified);
    }
}

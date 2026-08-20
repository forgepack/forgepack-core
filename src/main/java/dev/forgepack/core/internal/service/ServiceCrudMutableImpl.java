package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.api.service.ServiceCrudMutable;
import dev.forgepack.core.internal.model.EntityCrud;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.hateoas.RepresentationModel;
import java.util.UUID;

/**
 * Default implementation of {@link ServiceCrudMutable}.
 *
 * <p>Delegates persistence to a {@link RepositoryCrud} and conversion to a
 * {@link Mapper}. Enriches response DTOs with HATEOAS self links via
 * {@link #addHateoas(EntityCrud)}.</p>
 *
 * @param <Entity> domain entity type extending {@link EntityCrud}
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
public abstract class ServiceCrudMutableImpl<Entity extends EntityCrud, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends RepresentationModel<DTOResponse>>
    extends ServiceCrudReadImpl<Entity, DTORequest, DTOResponse>
    implements ServiceCrudMutable<Entity, DTORequest, DTOResponse> {

    private final Class<Entity> entity;
    private final RepositoryCrud<Entity> repositoryGeneric;
    private final Mapper<Entity, DTORequest, DTOResponse> mapper;

    public ServiceCrudMutableImpl(Class<Entity> entity, RepositoryCrud<Entity> repositoryGeneric, Mapper<Entity, DTORequest, DTOResponse> mapper) {
        super(entity, repositoryGeneric, mapper);
        this.entity = entity;
        this.repositoryGeneric = repositoryGeneric;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DTOResponse create(DTORequest created){
        Entity entity = repositoryGeneric.save(mapper.toEntity(created));
        addLog("create", entity.getId(), null, null);
        return addHateoas(entity);
    }

    @Override
    @Transactional
    public DTOResponse update(UUID id, DTORequest updated){
        Entity entity = existsEntity("update", id);
        mapper.updateEntity(updated, entity);
        Entity ratified = repositoryGeneric.save(entity);
        addLog("update", id, null, null);
        return addHateoas(ratified);
    }
}

package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.api.service.ServiceCrudMutable;
import dev.forgepack.core.api.service.ServiceCrudRead;
import dev.forgepack.core.internal.model.EntityCrud;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.apache.commons.beanutils.ConvertUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.data.domain.ExampleMatcher.matching;

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
public abstract class ServiceCrudReadImpl<Entity extends EntityCrud, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends RepresentationModel<DTOResponse>>
    extends ServiceUtils<Entity, DTORequest, DTOResponse>
    implements ServiceCrudRead<Entity, DTOResponse> {

    private final Class<Entity> entity;
    private final RepositoryCrud<Entity> repositoryGeneric;
    private final Mapper<Entity, DTORequest, DTOResponse> mapper;
    private static final Logger log = LoggerFactory.getLogger(ServiceCrudReadImpl.class);

    public ServiceCrudReadImpl(Class<Entity> entity, RepositoryCrud<Entity> repositoryGeneric, Mapper<Entity, DTORequest, DTOResponse> mapper) {
        super(entity, repositoryGeneric, mapper);
        this.entity = entity;
        this.repositoryGeneric = repositoryGeneric;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DTOResponse> findAll(Pageable pageable, String value, Class<Entity> entity) {
        String propertyName = pageable.getSort().stream()
                .findFirst()
                .map(Sort.Order::getProperty)
                .orElse("id");
        if ("id".equalsIgnoreCase(propertyName) && StringUtils.hasText(value)) {
            try {
                addLog("find all", null, propertyName, value);
                return repositoryGeneric.findById(UUID.fromString(value), pageable)
                        .map(this::addHateoas);
            } catch (IllegalArgumentException e){
                log.debug("Value '{}' is not a valid UUID, falling back to property search", value);
            }
        }
        try {
            Entity object = entity.getDeclaredConstructor().newInstance();
            ExampleMatcher exampleMatcher = matching()
                    .withIgnoreNullValues()
                    .withIgnoreCase()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
            Field field = ReflectionUtils.findField(entity, propertyName);
            String setterName = "set" + StringUtils.capitalize(propertyName);
            Method setter = object.getClass().getDeclaredMethod(setterName, field.getType());
            Object convertedValue = ConvertUtils.convert(value, field.getType());
            setter.invoke(object, convertedValue);
            Example<Entity> example = Example.of(object, exampleMatcher);
            return repositoryGeneric.findAll(example, pageable).map(this::addHateoas);
        } catch (Exception exception) {
            log.warn("Error searching {} by {}: {}", entity.getSimpleName(), propertyName, exception.getMessage());
            return repositoryGeneric.findAll(pageable).map(this::addHateoas);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DTOResponse findById(UUID id){
        Entity entity = existsEntity("find by ID", id);
        addLog("find by ID", id, null, null);
        return addHateoas(entity);
    }
}

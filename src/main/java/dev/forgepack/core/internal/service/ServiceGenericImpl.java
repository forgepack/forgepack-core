package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryGeneric;
import dev.forgepack.core.api.service.ServiceGeneric;
import dev.forgepack.core.internal.model.GenericAuditEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * Default implementation of {@link ServiceGeneric}.
 *
 * <p>Delegates persistence to a {@link RepositoryGeneric} and conversion to a
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
 * @see ServiceGeneric
 * @see RepositoryGeneric
 * @see Mapper
 */
public abstract class ServiceGenericImpl<Entity extends GenericAuditEntity, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends RepresentationModel<DTOResponse>> implements ServiceGeneric<Entity, DTORequest, DTOResponse> {

    private final Class<Entity> entity;
    private final RepositoryGeneric<Entity> repositoryGeneric;
    private final Mapper<Entity, DTORequest, DTOResponse> mapper;
    private static final Logger log = LoggerFactory.getLogger(ServiceGenericImpl.class);

    public ServiceGenericImpl(Class<Entity> entity, RepositoryGeneric<Entity> repositoryGeneric, Mapper<Entity, DTORequest, DTOResponse> mapper) {
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

    @Override
    @Transactional
    public DTOResponse update(UUID id, DTORequest updated){
        Entity entity = existsEntity("update", id);
        mapper.updateEntity(updated, entity);
        Entity ratified = repositoryGeneric.save(entity);
        addLog("update", id, null, null);
        return addHateoas(ratified);
    }

    @Override
    @Transactional(readOnly = false)
    public DTOResponse softDelete(UUID id){
        Entity entity = existsEntity("soft delete", id);
        entity.setDeletedAt(LocalDateTime.now());
        repositoryGeneric.save(entity);
        addLog("soft delete", id, null, null);
        return addHateoas(entity);
    }

    @Override
    @Transactional(readOnly = false)
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

    /**
     * Converts an entity to a {@link DTOResponse} and enriches it with a HATEOAS self link.
     *
     * <p>The self link is automatically generated using the entity name and
     * its identifier, following the pattern:</p>
     *
     * <pre>
     * /{entityName}/{id}
     * </pre>
     *
     * <p>The link is added using the {@link IanaLinkRelations#SELF} relation.</p>
     *
     * @param object entity instance
     * @return {@link DTOResponse} containing the entity data and HATEOAS self link
     */
    protected DTOResponse addHateoas(Entity object) {
        String entityName = Character.toLowerCase(entity.getSimpleName().charAt(0))
                + entity.getSimpleName().substring(1);
        String selfUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment(entityName, String.valueOf(object.getId()))
                .toUriString();
        return mapper.toResponse(object).add(Link.of(selfUri, IanaLinkRelations.SELF));
    }

    /**
     * Records a log entry for the given service action.
     *
     * <p>When {@code propertyName} is provided, a {@code DEBUG} entry is written
     * indicating a search by property. Otherwise, an {@code INFO} entry is written
     * identifying the current user, the action performed, and the target entity ID.</p>
     *
     * @param action       description of the operation (e.g., {@code "create"}, {@code "find by ID"})
     * @param id           identifier of the target entity, or {@code null} for search operations
     * @param propertyName name of the search property, or {@code null} for non-search operations
     * @param value        value used in the search, or {@code null} for non-search operations
     */
    protected void addLog(String action, UUID id, Object propertyName, Object value) {
        if(propertyName != null){
            log.debug("Retrieving {} with property: {}, value: {}", entity.getSimpleName(), propertyName, value);
        } else {
            log.info("A User {} entity with ID: {}", action, id);
        }
    }

    /**
     * Looks up a non-deleted entity by its identifier, throwing if absent.
     *
     * <p>Only entities whose {@code deletedAt} field is {@code null} are considered.
     * Used internally before any mutating operation to guarantee the entity exists.</p>
     *
     * @param action description of the calling operation, used in the exception message
     * @param id     identifier of the entity to look up
     * @return the found {@link Entity}
     * @throws EntityNotFoundException if no active entity exists with the given {@code id}
     */
    @Transactional(readOnly = true)
    protected Entity existsEntity(String action, UUID id) {
        return repositoryGeneric.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cannot %s: %s not found with ID %s", action, entity.getSimpleName(), id)));
    }
}

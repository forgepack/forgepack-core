package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.api.service.ServiceCrudMutable;
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
public class ServiceUtils<Entity extends GenericAuditEntity, DTORequest extends DTOIdentifiable<UUID>, DTOResponse extends RepresentationModel<DTOResponse>> {

    private final Class<Entity> entity;
    private final RepositoryCrud<Entity> repositoryGeneric;
    private final Mapper<Entity, DTORequest, DTOResponse> mapper;
    private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);

    public ServiceUtils(Class<Entity> entity, RepositoryCrud<Entity> repositoryGeneric, Mapper<Entity, DTORequest, DTOResponse> mapper) {
        this.entity = entity;
        this.repositoryGeneric = repositoryGeneric;
        this.mapper = mapper;
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

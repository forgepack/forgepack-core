package dev.forgepack.core.internal.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Base superclass for persistent entities providing auditing and soft delete support.
 *
 * <p>This class defines a standardized set of cross-cutting concerns for domain entities,
 * including identity management, automatic auditing, and logical deletion. It is designed
 * as a {@link MappedSuperclass}, allowing subclasses to inherit mappings without requiring
 * a dedicated table.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Unique identification using {@link UUID}</li>
 *     <li>Automatic temporal auditing (creation and last update timestamps)</li>
 *     <li>Author tracking (created by / modified by)</li>
 *     <li>Logical deletion (soft delete)</li>
 *     <li>Compatibility with Hibernate Envers auditing via {@link Audited}</li>
 * </ul>
 *
 * <h2>Auditing</h2>
 * <p>Auditing is integrated with Spring Data JPA through
 * {@link org.springframework.data.jpa.domain.support.AuditingEntityListener}.
 * An {@code AuditorAware} implementation must be configured in the application
 * context to automatically populate {@code createdBy} and {@code modifiedBy}.</p>
 *
 * <h2>Soft Delete</h2>
 * <p>Logical deletion is represented by the {@code deletedAt} field. When non-null,
 * the entity is considered deleted.</p>
 *
 * <h2>Equality</h2>
 * <p>The {@code equals} and {@code hashCode} implementations rely exclusively on the
 * entity identifier ({@code id}), assuming stable identity after persistence.</p>
 *
 * <h2>Considerations</h2>
 * <ul>
 *     <li>Avoid accessing {@code LAZY} associations outside of a transactional context</li>
 *     <li>Using {@code UUID} improves portability and supports distributed systems</li>
 *     <li>Soft delete does not enforce referential integrity at the database level</li>
 * </ul>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class GenericAuditEntity implements Serializable {

    /**
     * Unique identifier of the entity.
     *
     * <p>Automatically generated using {@link GenerationType#UUID}, ensuring global
     * uniqueness and independence from database-specific strategies.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    /**
     * Timestamp indicating when the entity was created.
     *
     * <p>Automatically set at persistence time and immutable thereafter.</p>
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating the last time the entity was updated.
     *
     * <p>Automatically updated on each modification.</p>
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Timestamp representing logical deletion (soft delete).
     *
     * <p>When non-null, the entity is considered deleted. Physical removal
     * is not performed automatically.</p>
     */
    private LocalDateTime deletedAt;

    /**
     * User responsible for creating the entity.
     *
     * <p>Automatically populated via {@link CreatedBy}, depending on the configured
     * {@code AuditorAware} implementation.</p>
     */
    @CreatedBy
    @JoinColumn(updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private String createdBy;
    /**
     * User responsible for the last modification of the entity.
     *
     * <p>Automatically updated via {@link LastModifiedBy}.</p>
     */
    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private String modifiedBy;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public String getModifiedBy() {
        return modifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericAuditEntity that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

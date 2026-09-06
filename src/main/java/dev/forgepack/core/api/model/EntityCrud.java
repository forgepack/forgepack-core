package dev.forgepack.core.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EntityCrud {

    public void setDeletedAt(LocalDateTime deletedAt);
    public UUID getId();
    public LocalDateTime getCreatedAt();
    public LocalDateTime getUpdatedAt();
    public LocalDateTime getDeletedAt();
    public String getCreatedBy();
    public String getModifiedBy();
    public boolean equals(Object o);
    public int hashCode();
}

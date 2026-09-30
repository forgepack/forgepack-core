package dev.forgepack.core.api.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntityCrudTest {

    @Test
    void equals_sameInstance_returnsTrue() {
        TestEntity entity = new TestEntity();

        assertThat(entity.equals(entity)).isTrue();
    }

    @Test
    void equals_null_returnsFalse() {
        assertThat(new TestEntity().equals(null)).isFalse();
    }

    @Test
    void equals_differentType_returnsFalse() {
        assertThat(new TestEntity().equals("not an entity")).isFalse();
    }

    @Test
    void equals_sameId_returnsTrueAndHashCodesMatch() {
        UUID id = UUID.randomUUID();
        TestEntity a = new TestEntity();
        TestEntity b = new TestEntity();
        ReflectionTestUtils.setField(a, "id", id);
        ReflectionTestUtils.setField(b, "id", id);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_differentId_returnsFalse() {
        TestEntity a = new TestEntity();
        TestEntity b = new TestEntity();
        ReflectionTestUtils.setField(a, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(b, "id", UUID.randomUUID());

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void gettersAndSetters_workAsExpected() {
        TestEntity entity = new TestEntity();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(entity, "id", id);
        ReflectionTestUtils.setField(entity, "createdAt", now);
        ReflectionTestUtils.setField(entity, "updatedAt", now);

        entity.setDeletedAt(now);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
        assertThat(entity.getDeletedAt()).isEqualTo(now);
    }

    static class TestEntity extends EntityCrud {}
}

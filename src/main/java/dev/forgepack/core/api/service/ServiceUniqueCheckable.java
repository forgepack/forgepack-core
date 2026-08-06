package dev.forgepack.core.api.service;

import dev.forgepack.validation.api.annotation.Unique;
import dev.forgepack.validation.api.validator.ValidatorUnique;

import java.util.UUID;

/**
 * Contract for services capable of performing uniqueness checks on entity fields.
 *
 * <p>This interface defines the operations required to verify whether a given
 * field value already exists in the persistence layer. It is primarily used by
 * the {@link ValidatorUnique} to
 * support the {@link Unique} Bean Validation
 * constraint.</p>
 *
 * <p>Implementations are typically provided by application services responsible
 * for managing domain entities.</p>
 *
 * <h3>Typical use cases</h3>
 * <ul>
 *     <li>Validating uniqueness during entity creation</li>
 *     <li>Validating uniqueness during entity updates while excluding the current entity</li>
 *     <li>Supporting custom unique field validation</li>
 * </ul>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see Unique
 * @see ValidatorUnique
 */
public interface ServiceUniqueCheckable {

    /**
     * Checks whether a record exists with the specified value in the given field.
     *
     * <p>This method is typically used during entity creation to ensure that
     * a field value is unique within the persistence layer.</p>
     *
     * @param field name of the field to be checked
     * @param value value to be searched for
     * @return {@code true} if a record exists with the specified value;
     *         {@code false} otherwise
     */
    boolean existsByField(String field, Object value);

    /**
     * Checks whether a record exists with the specified value in the given field,
     * excluding the record identified by the provided {@code id}.
     *
     * <p>This method is typically used during entity updates to ensure that the
     * new value remains unique while ignoring the entity currently being updated.</p>
     *
     * @param field name of the field to be checked
     * @param value value to be searched for
     * @param id identifier of the entity that should be excluded from the check
     * @return {@code true} if another record exists with the specified value;
     *         {@code false} otherwise
     */
    boolean existsByFieldAndIdNot(String field, Object value, UUID id);
}

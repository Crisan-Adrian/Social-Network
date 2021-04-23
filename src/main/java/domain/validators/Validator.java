package domain.validators;

public interface Validator<T> {

    /**
     * Validates an entity
     * @param entity Entity to be validated
     * @throws ValidationException if entity is invalid
     */
    void validate(T entity) throws ValidationException;
}
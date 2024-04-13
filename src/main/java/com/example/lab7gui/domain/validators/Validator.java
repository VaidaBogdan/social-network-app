package com.example.lab7gui.domain.validators;

public interface Validator<T> {
    /**
     *
     * @param entity - the entity we validate
     * @throws ValidationException - if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
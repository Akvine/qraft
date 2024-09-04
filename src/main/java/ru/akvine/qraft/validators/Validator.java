package ru.akvine.qraft.validators;

public interface Validator<T> {
    void validate(T value);
}

package com.bwl.apiusers.exceptions;

public class BaseNotFoundException extends RuntimeException {
    public BaseNotFoundException(Class<?> entityType, Integer id) {
        super("Could not find " + entityType.getSimpleName() + " with id: " + id);
    }
}

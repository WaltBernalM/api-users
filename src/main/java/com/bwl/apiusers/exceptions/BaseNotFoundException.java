package com.bwl.apiusers.exceptions;

import com.fasterxml.jackson.databind.ser.Serializers;

public class BaseNotFoundException extends RuntimeException {
    public BaseNotFoundException(Class<?> entityType, Integer id) {
        super("Could not find " + entityType.getSimpleName() + " with id: " + id);
    }

    public BaseNotFoundException(Class<?> entityType) {
        super("Could not find any " + entityType.getSimpleName() + "s that could match such fields");
    }

    public BaseNotFoundException(Class<?> entityType, String message) {
        super("Error in " + entityType.getSimpleName() + ": " + message);
    }
}

package com.bwl.apiusers.exceptions;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(Integer id) {
        super("Could not find permission " + id);
    }
}

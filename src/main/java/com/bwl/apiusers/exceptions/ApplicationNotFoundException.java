package com.bwl.apiusers.exceptions;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Integer id) {
        super("Could not find application " + id);
    }
}

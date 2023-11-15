package com.bwl.apiusers.exceptions;

import jakarta.persistence.criteria.CriteriaBuilder;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Integer id) {
        super("Could not find application " + id);
    }
}

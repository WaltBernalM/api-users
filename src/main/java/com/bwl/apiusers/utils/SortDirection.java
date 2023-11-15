package com.bwl.apiusers.utils;

import org.springframework.data.domain.Sort;

public class SortDirection {
    public static Sort.Direction get(String direction) {
        if (direction == null || direction.isEmpty() || direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Invalid sort direction: " + direction);
        }
    }
}

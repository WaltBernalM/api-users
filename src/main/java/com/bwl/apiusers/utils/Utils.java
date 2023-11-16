package com.bwl.apiusers.utils;

import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;

public class Utils {
    public static Sort.Direction getSortDirection(String direction) {
        if (direction == null || direction.isEmpty() || direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Invalid sort direction: " + direction);
        }
    }

    public static <T,D> D convertToDTO(T entity, Class<D> dtoClass) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            for (Field dtoField : dtoClass.getDeclaredFields()) {
                Field entitiyField = findMatchingField(entity.getClass(), dtoField.getName());
                if (entitiyField != null) {
                    entitiyField.setAccessible(true);
                    dtoField.setAccessible(true);
                    dtoField.set(dto, entitiyField.get(entity));
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to DTO");
        }
    }

    private static Field findMatchingField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}

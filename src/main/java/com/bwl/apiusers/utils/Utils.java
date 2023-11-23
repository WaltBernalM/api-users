package com.bwl.apiusers.utils;

import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.models.User;
import com.bwl.apiusers.models.UserComposedModel;
import com.bwl.apiusers.repositories.BaseRepository;
import com.bwl.apiusers.repositories.UserComposedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public static <T, R extends BaseRepository<T>> Page<T> generateEntityPage(String name, Pageable pagingSort, R repository) {
        Page<T> entityPage;
        if(name == null) {
            return entityPage = repository.findAll(pagingSort);
        }
        return entityPage = repository.findByNameContaining(name, pagingSort);
    }

    public static <D, T> void parseResponseData(List<T> entities, Class<D> dtoEntityClass, Map<String, Object> response) {
        if(dtoEntityClass.isInterface()) {
            response.put("data", entities);
        } else {
            List<D> dtoEntities = new ArrayList<>();
            for (T ent : entities) {
                D dto = Utils.convertToDTO(ent, dtoEntityClass);
                dtoEntities.add(dto);
            }
            response.put("data", dtoEntities);
        }
    }

    public static <T,D> D convertToDTO(T entity, Class<D> dtoClass) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            for (Field dtoField : dtoClass.getDeclaredFields()) {
                Field entityField = findMatchingField(entity.getClass(), dtoField.getName());
                if (entityField != null) {
                    entityField.setAccessible(true);
                    dtoField.setAccessible(true);
                    if (isForeignKeyModel(entityField) && entityField.getName().length() > 2 && entityField.getName().contains("id")) {
                        Object entityFieldValue = entityField.get(entity);
                        Field idField = findMatchingField(entityFieldValue.getClass(), "id");
                        assert idField != null;
                        idField.setAccessible(true);
                        Integer idValue = (Integer) idField.get(entityFieldValue);
                        dtoField.set(dto, idField.get(entityFieldValue));
                    } else {
                        dtoField.set(dto, entityField.get(entity));
                    }
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to DTO");
        }
    }

    public static Field findMatchingField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public static <T extends UserComposedModel, R extends UserComposedRepository<T>> void deleteUserComposedEntities(R repository, User userToDelete) {
        List<T> currentUserEntities = repository.findAllByIdUser(userToDelete);
        List<Integer> currentUserEntityIds = currentUserEntities.stream()
                .map(UserComposedModel::getId)
                .toList();
        for (Integer userEntityId : currentUserEntityIds) {
            repository.deleteById(userEntityId);
        }
    }

    public static <T, R extends BaseRepository<T>> Optional<T> verifyExistence(Integer id, R repository, Class<T> entityClass) {
        Optional<T> entityInDb = repository.findById(id);
        if(entityInDb.isEmpty()) {
            throw new BaseNotFoundException(entityClass, "provided id " + id + " not found in database");
        }
        return entityInDb;
    }

    private static boolean isForeignKeyModel(Field field) {
        return !field.getType().isPrimitive() && !field.getType().isAssignableFrom(String.class);
    }
}

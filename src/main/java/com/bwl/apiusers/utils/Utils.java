package com.bwl.apiusers.utils;

import com.bwl.apiusers.models.Profile;
import com.bwl.apiusers.models.User;
import com.bwl.apiusers.models.UserComposedModel;
import com.bwl.apiusers.models.UserProfile;
import com.bwl.apiusers.repositories.BaseRepository;
import com.bwl.apiusers.repositories.UserComposedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}

package com.bwl.apiusers.dtos;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PermissionDTO implements DTO {
    private Integer id;
    private String name;
    private String description;
    private String keycode;
}

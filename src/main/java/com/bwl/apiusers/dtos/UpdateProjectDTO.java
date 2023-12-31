package com.bwl.apiusers.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UpdateProjectDTO implements DTO {
    private String name;
    private String keycode;
    private String description;
    private Integer idClient;
    private Boolean enabled;
    private Integer idApplication;
}

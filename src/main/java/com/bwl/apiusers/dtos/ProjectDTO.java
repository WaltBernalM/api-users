package com.bwl.apiusers.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
public class ProjectDTO implements DTO {
    private Integer id;
    private String name;
    private String keycode;
    private Date creationDate;
    private String description;
    private Integer idClient;
    private Boolean enabled;
}

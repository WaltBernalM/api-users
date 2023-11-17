package com.bwl.apiusers.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UpdateUserDTO implements DTO {
    private String email;
    private String name;
    private String userName;
    private Integer[] idProfiles;
}

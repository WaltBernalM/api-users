package com.bwl.apiusers.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class NewUserDTO implements DTO {
    private String email;
    private String name;
    private String username;
    private Integer profileId;
    private Integer applicationId;
    private String password;

    public NewUserDTO(){}
}

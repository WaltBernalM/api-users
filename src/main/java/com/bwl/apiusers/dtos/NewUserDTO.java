package com.bwl.apiusers.dtos;

import lombok.Data;

@Data
public class NewUserDTO implements DTO {
    private String email;
    private String name;
    private String userName;
    private Integer profileId;
    private Integer applicationId;
    private String password;
}

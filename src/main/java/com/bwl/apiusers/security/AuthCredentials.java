package com.bwl.apiusers.security;

import lombok.Data;

@Data
public class AuthCredentials {
    private String userName;
    private String password;
    private Integer idApplication;
}

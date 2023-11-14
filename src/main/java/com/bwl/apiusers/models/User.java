package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;

@Entity
@Data
@Table(name="USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "Enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "FORCE")
    private Boolean force;

    @Column(name = "HASH")
    private String hash;

    @Column(name = "HASH2FA")
    private String hash2FA;

    @Column(name = "LAST_JWT")
    private String lastJwt;

    @Column(name = "LAST_PASSWORD_CHANGE")
    private Date lastPasswordChange;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "PRE_AUTH")
    private Date preAuth;

    @Column(name = "SUPERUSER", nullable = false)
    private Boolean superUser;

    @Column(name = "TWO_FACTOR_AUTH")
    private Boolean twoFactorAuth;

    @Column(name = "TWO_FACTOR_AUTH_LIMIT")
    private Date twoFactorAuthLimit;

    @Column(name = "USERNAME", nullable = false)
    private String userName;

    @Column(name= "LAST_LOGIN_TIME")
    private Date lastLoginTime;

    @Column(name = "RECOVERY_TOKEN")
    private String recoveryToken;

    @Column(name = "CREDENTIALS_EXPIRED")
    private Boolean credentialsExpired;

    @Column(name = "CREATION_DATE")
    private Date creationDate;
}

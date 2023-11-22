package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "PROFILES_PERMISSIONS")
public class ProfilePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_PROFILE", nullable = false)
    private Profile idProfile;

    @ManyToOne
    @JoinColumn(name = "ID_PERMISSION", nullable = false)
    private Permission idPermission;
}

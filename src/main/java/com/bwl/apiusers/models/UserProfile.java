package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name="USER_PROFILES")
public class UserProfile implements UserComposedModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User idUser;

    @ManyToOne
    @JoinColumn(name = "ID_PROFILE", nullable = false)
    private Profile idProfile;
}

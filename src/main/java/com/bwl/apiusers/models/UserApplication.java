package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "USERS_APPLICATIONS")
public class UserApplication implements UserComposedModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User idUser;

    @ManyToOne
    @JoinColumn(name = "ID_APPLICATION", nullable = false)
    private Application idApplication;
}


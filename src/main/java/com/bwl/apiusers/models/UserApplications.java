package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "USERS_APPLICATIONS")
public class UserApplications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User idUser;

    @ManyToOne
    @JoinColumn(name = "ID_APPLICATION", nullable = false)
    private Application idApplication;
}


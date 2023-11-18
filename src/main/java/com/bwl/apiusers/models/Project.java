package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "PROJECTS")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "KEYCODE", nullable = false)
    private String keycode;

    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @ManyToOne
    @JoinColumn(name = "ID_CLIENT", nullable = false)
    private Application idApplication;
}

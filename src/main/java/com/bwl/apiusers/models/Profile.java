package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "PROFILES")
public class Profile implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "KEYCODE", nullable = false)
    private String keycode;

    @ManyToOne
    @JoinColumn(name = "ID_APPLICATION", nullable = false)
    private Application idApplication;
}

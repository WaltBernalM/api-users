package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "CLIENTS")
public class Client implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "SHORT_NAME", nullable = false)
    private String shortName;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;
}

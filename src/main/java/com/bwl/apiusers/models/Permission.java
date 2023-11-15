package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

@Table(name="PERMISSIONS")
@Data
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "KEYCODE", nullable = false)
    private String keycode;

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", keycode='" + keycode + '\'' +
                '}';
    }
}

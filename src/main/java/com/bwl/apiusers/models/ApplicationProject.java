package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "APPLICATIONS_PROJECTS")
public class ApplicationProject implements ComposedModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_APPLICATION", nullable = false)
    private Application idApplication;

    @ManyToOne
    @JoinColumn(name = "ID_PROJECT", nullable = false)
    private Project idProject;
}

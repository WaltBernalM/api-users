package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.ApplicationProject;
import com.bwl.apiusers.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationProjectRepository extends JpaRepository<ApplicationProject, Integer> {
    List<ApplicationProject> findAllByIdProject(Project idProject);
    Optional<ApplicationProject> findOneByIdProject(Project idProject);
}

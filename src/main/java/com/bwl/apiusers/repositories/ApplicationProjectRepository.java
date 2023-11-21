package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.models.ApplicationProject;
import com.bwl.apiusers.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationProjectRepository extends JpaRepository<ApplicationProject, Integer> {
    List<ApplicationProject> findAllByIdProject(Project idProject);
    List<ApplicationProject> findAllByIdApplication(Application idApplication);
}

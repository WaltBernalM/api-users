package com.bwl.apiusers.controllers;


import com.bwl.apiusers.dtos.NewProjectDTO;
import com.bwl.apiusers.dtos.UpdateProjectDTO;
import com.bwl.apiusers.services.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController implements GenericCRUDController<NewProjectDTO, UpdateProjectDTO> {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<?> postNew(NewProjectDTO newProjectDTO) {
        return service.newProjectSignup(newProjectDTO);
    }

    @Override
    public ResponseEntity<?> updateOne(Integer id, UpdateProjectDTO updateProjectDTO) {
        return service.updateProject(id, updateProjectDTO);
    }

    @Override
    public ResponseEntity<?> deleteOne(Integer id) {
        return service.deleteProject(id);
    }

    @Override
    public ResponseEntity<?> one(Integer id) {
        return service.one(id);
    }

    @Override
    public ResponseEntity<?> all(String name, int page, int size, String[] sort) {
        return service.all(name, page, size, sort);
    }
}

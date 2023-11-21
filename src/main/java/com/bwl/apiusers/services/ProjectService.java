package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.ProjectModelAssembler;
import com.bwl.apiusers.dtos.NewProjectDTO;
import com.bwl.apiusers.dtos.ProjectDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.models.ApplicationProject;
import com.bwl.apiusers.models.Client;
import com.bwl.apiusers.models.Project;
import com.bwl.apiusers.repositories.ApplicationProjectRepository;
import com.bwl.apiusers.repositories.ApplicationRepository;
import com.bwl.apiusers.repositories.ClientRepository;
import com.bwl.apiusers.repositories.ProjectRepository;
import com.bwl.apiusers.utils.Utils;
import jdk.jshell.execution.Util;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectService extends GenericReadService<Project, ProjectDTO, ProjectRepository, ProjectModelAssembler> {
    private final ProjectRepository projectRepository;
    private final ProjectModelAssembler assembler;
    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationProjectRepository applicationProjectRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectModelAssembler assembler,
            ClientRepository clientRepository,
            ApplicationRepository applicationRepository,
            ApplicationProjectRepository applicationProjectRepository
    ){
        super(projectRepository, assembler, Project.class, ProjectDTO.class);
        this.projectRepository = projectRepository;
        this.assembler = assembler;
        this.clientRepository  = clientRepository;
        this.applicationRepository = applicationRepository;
        this.applicationProjectRepository = applicationProjectRepository;
    }

    public ResponseEntity<?> newProjectSignup(@RequestBody NewProjectDTO newProjectDTO) {
        try {
            String newProjectDTOKeycode = newProjectDTO.getKeycode();
            Optional<Project> projectInDB = projectRepository.findOneByKeycode(newProjectDTOKeycode);

            // Verify if id of client exists
            Integer clientId = newProjectDTO.getIdClient();
            Optional<Client> clientInDb = Utils.verifyExistence(clientId, clientRepository, Client.class);

            // verify if id of application exists
            Integer applicationId = newProjectDTO.getIdApplication();
            Optional<Application> applicationInDb = Utils.verifyExistence(applicationId, applicationRepository, Application.class);

            if (projectInDB.isEmpty()) {
                Project newProject = parseToProject(newProjectDTO, clientInDb.get());
                Project savedProject = projectRepository.save(newProject);
                addNewApplicationProjectRow(savedProject, applicationInDb.get());

                Map<String, Object> body = new HashMap<>();
                ProjectDTO dto = Utils.convertToDTO(savedProject, ProjectDTO.class);
                body.put("project", dto);

                Map<String, Object> response = new HashMap<>();
                response.put("data", body);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            throw new BaseNotFoundException(Project.class, "Keycode already exists in database");

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> one(@PathVariable Integer id) {
        try {
            Project project = projectRepository.findById(id).orElseThrow(() -> new BaseNotFoundException(Project.class, id));
            ProjectDTO dto = Utils.convertToDTO(project, ProjectDTO.class);
            EntityModel<ProjectDTO> entityModel = assembler.toModel(dto);
            return ResponseEntity.ok(entityModel);
        } catch( Exception e) {
            ErrorResponse errorresponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorresponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Project parseToProject(NewProjectDTO newProjectDTO, Client idClient) {
        Project newProject = new Project();
        newProject.setName(newProjectDTO.getName());
        newProject.setKeycode(newProjectDTO.getKeycode());
        newProject.setDescription(newProjectDTO.getDescription());
        newProject.setIdClient(idClient);
        newProject.setCreationDate(new Date());
        newProject.setEnabled(true);

        return newProject;
    }

    // Method to add New ApplicationProject row
    private void addNewApplicationProjectRow(Project project, Application application) {
        ApplicationProject newApplicationProjectRow = new ApplicationProject();
        newApplicationProjectRow.setIdApplication(application);
        newApplicationProjectRow.setIdProject(project);
        applicationProjectRepository.save(newApplicationProjectRow);
    }
}

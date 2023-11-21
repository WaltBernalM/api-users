package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.ProjectModelAssembler;
import com.bwl.apiusers.dtos.NewProjectDTO;
import com.bwl.apiusers.dtos.ProjectDTO;
import com.bwl.apiusers.dtos.UpdateProjectDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.ApplicationProjectRepository;
import com.bwl.apiusers.repositories.ApplicationRepository;
import com.bwl.apiusers.repositories.ClientRepository;
import com.bwl.apiusers.repositories.ProjectRepository;
import com.bwl.apiusers.utils.Utils;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Field;
import java.util.*;

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
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>(errorresponse, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> updateProject(@PathVariable Integer id, @RequestBody UpdateProjectDTO updateProjectDTO) {
        try {
            Project projectInDb = projectRepository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(Project.class, "id not found in database"));

            // Verify if Keycode already taken
            String updateDTOKeycode = updateProjectDTO.getKeycode();
            List<Project> projectsInDb = projectRepository.findAllByKeycode(updateDTOKeycode);
            if (!projectsInDb.isEmpty()) {
                Project first = projectsInDb.getFirst();
                if (!Objects.equals(first.getId(), id)) {
                    throw new BaseNotFoundException(Project.class, "keycode already taken by another Project");
                }
            }

            // Verify if idClient do exists.
            Integer clientId = updateProjectDTO.getIdClient();
            Optional<Client> clientInDb = Optional.empty();
            if (clientId != null) {
                clientInDb = Utils.verifyExistence(clientId, clientRepository, Client.class);
            }

            // Verify if idApplication do exists.
            Integer applicationId = updateProjectDTO.getIdApplication();
            Optional<Application> applicationInDb = Optional.empty();
            if (applicationId != null) {
                applicationInDb = Utils.verifyExistence(applicationId, applicationRepository, Application.class);
            }

            // Find all projects with idProject and save if it doesn't exist
            List<ApplicationProject> allApplicationsRelatedToProject = applicationProjectRepository.findAllByIdProject(projectInDb);

            // Block to save the ApplicationProject if is not duplicated
            boolean isDuplicated = false;
            for (ApplicationProject row: allApplicationsRelatedToProject) {
                Project project = row.getIdProject();
                Application application = row.getIdApplication();
                if (project.equals(projectInDb) && applicationInDb.isPresent() && application.equals(applicationInDb.get())) {
                    isDuplicated = true;
                }
            }
            if (!isDuplicated && applicationInDb.isPresent()) {
                addNewApplicationProjectRow(projectInDb, applicationInDb.get());
            }

            Map<String, Object> body = new HashMap<>();

            // Update of projectInDB values as of updateProjectDTO
            for(Field field : updateProjectDTO.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(updateProjectDTO);
                if (value != null && !field.getName().equals("idClient")) {
                    Field projectField = Project.class.getDeclaredField(field.getName());
                    projectField.setAccessible(true);
                    projectField.set(projectInDb, value);
                }
            }
            clientInDb.ifPresent(projectInDb::setIdClient);

            Project updatedProject = projectRepository.save(projectInDb);
            ProjectDTO updatedProjectData = Utils.convertToDTO(updatedProject, ProjectDTO.class);

            body.put("project", updatedProjectData);

            Map<String, Object> response = new HashMap<>();
            response.put("data", body);
            return new ResponseEntity<>(response, HttpStatus.OK) ;
        } catch (Exception e) {
            ErrorResponse errorresponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorresponse, HttpStatus.BAD_REQUEST);
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

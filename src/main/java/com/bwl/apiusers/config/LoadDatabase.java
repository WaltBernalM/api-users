package com.bwl.apiusers.config;

import com.bwl.apiusers.dtos.ProjectDTO;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoadDatabase implements Loader {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private List<Client> clients = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<Application> applications = new ArrayList<>();
    private List<ApplicationProject> applicationProjects = new ArrayList<>();
    private List<Profile> profiles = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private List<ProfilePermission> profilePermissions = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<UserApplication> userApplications = new ArrayList<>();
    private List<UserProfile> userProfiles = new ArrayList<>();

    @Bean
    @Override
    public CommandLineRunner initDatabase(
            ClientRepository clientRepository,
            ProjectRepository projectRepository,
            ApplicationRepository applicationRepository,
            ApplicationProjectRepository applicationProjectRepository,
            ProfileRepository profileRepository,
            PermissionRepository permissionRepository,
            ProfilePermissionRepository profilePermissionRepository,
            UserRepository userRepository,
            UserApplicationRepository userApplicationRepository,
            UserProfileRepository userProfileRepository) {

        TypeReference<List<Client>> clientTypeReference = new TypeReference<List<Client>>() {};
        jsonParser(clientRepository, clientTypeReference, clients, "clients.json");

        TypeReference<List<Project>> projectTypeReference = new TypeReference<List<Project>>() {};
        jsonParser(projectRepository, projectTypeReference, projects,"projects.json");

        TypeReference<List<Application>> applicationTypeReference = new TypeReference<List<Application>>() {};
        jsonParser(applicationRepository, applicationTypeReference, applications, "applications.json");

        TypeReference<List<Profile>> profileTypeReference = new TypeReference<List<Profile>>() {};
        jsonParser(profileRepository, profileTypeReference, profiles, "profiles.json");

        TypeReference<List<Permission>> permissionTypeReference = new TypeReference<List<Permission>>() {};
        jsonParser(permissionRepository, permissionTypeReference, permissions, "permissions.json");

        TypeReference<List<User>> userTypeReference = new TypeReference<List<User>>() {};
        jsonParser(userRepository, userTypeReference, users, "users.json");

        return args -> {
            clientRepository.findAll().forEach(client -> log.info("Preloaded Client: " + client.getShortName()));
            projectRepository.findAll().forEach(project -> log.info("Preloaded Project: " + project.getKeycode()));
            applicationRepository.findAll().forEach(application -> log.info("Preloaded Client: " + application.getKeycode()));
            profileRepository.findAll().forEach(profile -> log.info("Preloaded Profile: " + profile.getKeycode()));
            permissionRepository.findAll().forEach(permission -> log.info("Preloaded Permission: " + permission.getKeycode()));
            userRepository.findAll().forEach(user -> log.info("Preloaded User: " + user.getUsername()));
        };
    }
 }

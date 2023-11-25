package com.bwl.apiusers.config;

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

    private final List<Client> clients = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<Application> applications = new ArrayList<>();
    private final List<ApplicationProject> applicationProjects = new ArrayList<>();
    private final List<Profile> profiles = new ArrayList<>();
    private final List<Permission> permissions = new ArrayList<>();
    private final List<ProfilePermission> profilePermissions = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<UserApplication> userApplications = new ArrayList<>();
    private final List<UserProfile> userProfiles = new ArrayList<>();

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
        jsonBaseModelParser(clientRepository, clientTypeReference, clients, "clients.json");

        TypeReference<List<Project>> projectTypeReference = new TypeReference<List<Project>>() {};
        jsonBaseModelParser(projectRepository, projectTypeReference, projects,"projects.json");

        TypeReference<List<Application>> applicationTypeReference = new TypeReference<List<Application>>() {};
        jsonBaseModelParser(applicationRepository, applicationTypeReference, applications, "applications.json");

        TypeReference<List<Profile>> profileTypeReference = new TypeReference<List<Profile>>() {};
        jsonBaseModelParser(profileRepository, profileTypeReference, profiles, "profiles.json");

        TypeReference<List<Permission>> permissionTypeReference = new TypeReference<List<Permission>>() {};
        jsonBaseModelParser(permissionRepository, permissionTypeReference, permissions, "permissions.json");

        TypeReference<List<User>> userTypeReference = new TypeReference<List<User>>() {};
        jsonBaseModelParser(userRepository, userTypeReference, users, "users.json");

        TypeReference<List<UserApplication>> userApplicationTypeReference = new TypeReference<List<UserApplication>>() {};
        jsonComposedModelParser(userApplicationRepository, userApplicationTypeReference, userApplications, "userApplications.json");

        TypeReference<List<ProfilePermission>> profilePermissionTypeReference = new TypeReference<List<ProfilePermission>>() {};
        jsonComposedModelParser(profilePermissionRepository, profilePermissionTypeReference, profilePermissions, "profilePermissions.json");

        TypeReference<List<UserProfile>> userProfileTypeReference = new TypeReference<List<UserProfile>>() {};
        jsonComposedModelParser(userProfileRepository, userProfileTypeReference, userProfiles, "userProfiles.json");

        TypeReference<List<ApplicationProject>> applicationProjectTypeReference = new TypeReference<List<ApplicationProject>>() {};
        jsonComposedModelParser(applicationProjectRepository, applicationProjectTypeReference, applicationProjects, "applicationProjects.json");

        return args -> {
            clientRepository.findAll().forEach(client -> log.info("Preloaded Client: " + client.getShortName()));
            projectRepository.findAll().forEach(project -> log.info("Preloaded Project: " + project.getKeycode()));
            applicationRepository.findAll().forEach(application -> log.info("Preloaded Client: " + application.getKeycode()));
            profileRepository.findAll().forEach(profile -> log.info("Preloaded Profile: " + profile.getKeycode()));
            permissionRepository.findAll().forEach(permission -> log.info("Preloaded Permission: " + permission.getKeycode()));
            userRepository.findAll().forEach(user -> log.info("Preloaded User: " + user.getUsername()));
            userApplicationRepository.findAll().forEach(userApp -> log.info("Preloaded UserApplication: " + userApp));
            profilePermissionRepository.findAll().forEach(profilePermission -> log.info("Preloaded ProfilePermission: " + profilePermission));
            userProfileRepository.findAll().forEach(userProfile -> log.info("Preloaded UserProfile: " + userProfile));
            applicationProjectRepository.findAll().forEach(applicationProject -> log.info("Preloaded ApplicationProject: " + applicationProject));
        };
    }
 }

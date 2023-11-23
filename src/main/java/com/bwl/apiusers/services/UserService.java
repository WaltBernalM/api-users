package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.UserModelAssembler;
import com.bwl.apiusers.dtos.NewUserDTO;
import com.bwl.apiusers.dtos.UpdateUserDTO;
import com.bwl.apiusers.dtos.UserDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.*;
import com.bwl.apiusers.utils.Utils;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Field;
import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class UserService extends GenericReadService<User, UserDTO, UserRepository, UserModelAssembler> {
    private final UserRepository userRepository;
    private final UserModelAssembler assembler;
    private final ProfileRepository profileRepository;
    private final ApplicationRepository applicationRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserApplicationRepository userApplicationRepository;

    public UserService(
            ProfileRepository profileRepository,
            UserModelAssembler assembler,
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            UserProfileRepository userProfileRepository,
            UserApplicationRepository userApplicationRepository) {
        super(userRepository, assembler, User.class, UserDTO.class);
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.profileRepository = profileRepository;
        this.applicationRepository = applicationRepository;
        this.userProfileRepository = userProfileRepository;
        this.userApplicationRepository = userApplicationRepository;
    }

    public ResponseEntity<?> newUserSignup(@RequestBody NewUserDTO newUserDTO) {
        try {
            String newUserDTOUserName = newUserDTO.getUsername();

            userCanCreate(newUserDTO.getApplicationId());

            Optional<User> userInDb = userRepository.findOneByUsername(newUserDTOUserName);

            Integer profileId = newUserDTO.getProfileId();
            Optional<Profile> profileInDb = Utils.verifyExistence(profileId, profileRepository, Profile.class);

            Integer applicationId = newUserDTO.getApplicationId();
            Optional<Application> applicationInDb = Utils.verifyExistence(applicationId, applicationRepository, Application.class);

            if (profileInDb.isPresent() && applicationInDb.isPresent()) {
                Application app = applicationInDb.get();
                Profile profile = profileInDb.get();
                if (!Objects.equals(app.getId(), profile.getIdApplication().getId())) {
                    throw new BaseNotFoundException(
                            Profile.class,
                            "Profile with id " + profile.getId() + " is not related to Application with id " + app.getId()
                    );
                }
            }

            if (userInDb.isEmpty()) {
                User newUser = parseToUser(newUserDTO);
                User savedUser = userRepository.save(newUser);
                addNewUserProfileRow(savedUser, profileInDb.get());
                addNewUserApplicationRow(savedUser, applicationInDb.get());

                Map<String, Object> body = new HashMap<>();

                body.put("user", newUser);
                body.put("idProfile", profileId);
                body.put("idApplication", applicationId);

                Map<String, Object> response = new HashMap<>();
                response.put("data", body);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            throw new BaseNotFoundException(User.class, "UserName already exists in database");

        } catch(Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> one(@PathVariable Integer id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new BaseNotFoundException(User.class, id));
            UserDTO dto = Utils.convertToDTO(user, UserDTO.class);
            EntityModel<UserDTO> entityModel = assembler.toModel(dto);

            return ResponseEntity.ok(entityModel);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            // Verify if User id exists in db
            User userInDb = userRepository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(User.class, "id not found in database"));

            // Verify if user accessing can edit the resource
            userCanEdit(userInDb);

            // Verify if userName is already taken
            String updateUserDTOUserName = updateUserDTO.getUsername();
            List<User> usernamesInDb = userRepository.findAllByUsername(updateUserDTOUserName);
            if (!usernamesInDb.isEmpty()) {
                User first = usernamesInDb.get(0);
                if (!Objects.equals(first.getId(), id)) {
                    throw new BaseNotFoundException(User.class, "username is already taken by another User");
                }
            }

            // Verify the ProfileId array passed in body
            boolean updateProfiles = false;
            Optional<Integer[]> userProfilesToUpdate = Optional.ofNullable(updateUserDTO.getIdProfiles());
            List<Profile> profilesToSet = new ArrayList<>();
            if (userProfilesToUpdate.isPresent()) {
                for (Integer profileId : userProfilesToUpdate.get()) {
                    Profile profile = Utils.verifyExistence(profileId, profileRepository, Profile.class).get();
                    updateProfiles = true;
                    profilesToSet.add(profile);
                }
            }

            Map<String, Object> body = new HashMap<>();

            // Update UserProfile table for new fields in idProfiles
            if (updateProfiles) {
                // Block to delete the currentUserProfiles
                Utils.deleteUserComposedEntities(userProfileRepository, userInDb);

                // Block to update the new UserProfiles
                for (Profile profileToSet: profilesToSet) {
                    UserProfile newUserProfile = new UserProfile();
                    newUserProfile.setIdProfile(profileToSet);
                    newUserProfile.setIdUser(userInDb);
                    userProfileRepository.save(newUserProfile);
                }
            }

            // Update of userInDb values as of updateUserDTO
            for (Field field : updateUserDTO.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(updateUserDTO);
                if (value != null && !field.getName().equals("idProfiles")) {
                    Field userField = User.class.getDeclaredField(field.getName());
                    userField.setAccessible(true);
                    userField.set(userInDb, value);
                }
            }
            User updatedUser = userRepository.save(userInDb);

            UserDTO updatedUserData = Utils.convertToDTO(updatedUser, UserDTO.class);
            body.put("user", updatedUserData);

            Map<String, Object> response = new HashMap<>();
            response.put("data", body);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            } else if (e instanceof AccessDeniedException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            User userToDelete = userRepository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(User.class, "id was not found in database"));

            // Verify if user accessing can edit the resource
            userCanEdit(userToDelete);

            // Delete UserProfiles related to User
            Utils.deleteUserComposedEntities(userProfileRepository, userToDelete);

            // Delete UserApplications related to User
            Utils.deleteUserComposedEntities(userApplicationRepository, userToDelete);

            userRepository.deleteById(id);

            Map<String, Object> body = new HashMap<>();
            UserDTO userDeleted = Utils.convertToDTO(userToDelete, UserDTO.class);
            body.put("user", userDeleted);
            Map<String, Object> response = new HashMap<>();
            response.put("data", body);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch(Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            } else if (e instanceof AccessDeniedException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User parseToUser(NewUserDTO newUserDTO) {
        String userPassword = newUserDTO.getPassword();
        String passwordHash = new BCryptPasswordEncoder().encode(userPassword);

        User newUser = new User();
        newUser.setEmail(newUserDTO.getEmail());
        newUser.setEnabled(true);
        newUser.setForce2FA(false);
        newUser.setHash(passwordHash);
        newUser.setHash2FA("");
        newUser.setLastJwt("");
        newUser.setLastPasswordChange(new Date());
        newUser.setName(newUserDTO.getName());
        newUser.setPreAuth(null);
        newUser.setSuperUser(false);
        newUser.setTwoFactorAuth(false);
        newUser.setTwoFactorAuthLimit(null);
        newUser.setUsername(newUserDTO.getUsername());
        newUser.setLastLoginTime(null);
        newUser.setRecoveryToken(null);
        newUser.setCredentialsExpired(false);
        newUser.setCreationDate(new Date());

        return newUser;
    }

    // Method to add New UserProfile row
    private void addNewUserProfileRow(User user, Profile profile) {
        UserProfile userProfile = new UserProfile();
        userProfile.setIdUser(user);
        userProfile.setIdProfile(profile);
        userProfileRepository.save(userProfile);
    }

    // Method to add New UserApplication row
    private void addNewUserApplicationRow(User user, Application application) {
        UserApplication userApplication = new UserApplication();
        userApplication.setIdUser(user);
        userApplication.setIdApplication(application);
        userApplicationRepository.save(userApplication);
    }

    private void userCanEdit(User userInDb) throws AccessDeniedException {
        Integer userAppAuthorityId = Utils.getUserAppAuthorityId();
        UserApplication userApp = userApplicationRepository.findOneByIdUser(userInDb)
                .orElseThrow(() -> new BaseNotFoundException(User.class, "User not related to any application"));
        if (!userAppAuthorityId.equals(userApp.getIdApplication().getId())) {
            System.out.println("Cannot edit");
            throw new AccessDeniedException("Resource not available for modification");
        }
        System.out.println("Can edit");
    }

    private void userCanCreate(Integer idApplication) throws AccessDeniedException {
        Integer userAppAuthorityId = Utils.getUserAppAuthorityId();
        if(!userAppAuthorityId.equals(idApplication)) {
            System.out.println("Cannot create");
            throw new AccessDeniedException("Unauthorized for resource creation");
        }
        System.out.println("Can create");
    }
}

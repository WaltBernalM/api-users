package com.bwl.apiusers.controllers;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping("api/users")
public class UserController extends GenericReadController<User, UserDTO, UserRepository, UserModelAssembler> {
    private final UserRepository userRepository;
    private final UserModelAssembler assembler;
    private final ProfileRepository profileRepository;
    private final ApplicationRepository applicationRepository;
    private final UserProfileRepository userProfileRepository;
    private  final UserApplicationRepository userApplicationRepository;

    public UserController(
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

    @PostMapping("/signup")
    public ResponseEntity<?> newUserSignup(@RequestBody NewUserDTO newUserDTO) {
        try {
            String newUserDTOUserName = newUserDTO.getUserName();
            Optional<User> userInDb = userRepository.findOneByEmail(newUserDTOUserName);

            Integer profileId = newUserDTO.getProfileId();
            Optional<Profile> profileInDb = profileRepository.findById(profileId);
            if (profileInDb.isEmpty()) {
                throw new BaseNotFoundException(Profile.class, "provided id not found in database");
            }

            Integer applicationId = newUserDTO.getApplicationId();
            Optional<Application> applicationInDb = applicationRepository.findById(applicationId);
            if (applicationInDb.isEmpty()) {
                throw new BaseNotFoundException(Application.class, "provided id not found in database");
            }

            if (userInDb.isEmpty()) {
                User newUser = parseToUser(newUserDTO);
                User savedUser = userRepository.save(newUser);
                UserProfile savedUserProfile = addNewUserProfileRow(savedUser, profileInDb.get());
                UserApplication savedUserApplication = addNewUserApplicationRow(savedUser, applicationInDb.get());

                Map<String, Object> body = new HashMap<>();

                body.put("user", newUser);
                body.put("idProfile", profileId);
                body.put("idApplication", applicationId);

                Map<String, Object> response = new HashMap<>();
                response.put("data", body);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            throw new BaseNotFoundException(User.class, "email already exists in database");

        } catch(Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> one(@PathVariable Integer id) {
        try {
            User user = getRepository().findById(id).orElseThrow(() -> new BaseNotFoundException(Profile.class, id));
            UserDTO dto = Utils.convertToDTO(user, UserDTO.class);
            EntityModel<UserDTO> entityModel = assembler.toModel(dto);

            return ResponseEntity.ok(entityModel);
        } catch (Exception e) {
            ErrorResponse errorresponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorresponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            // Verify if User id exists in db
            User userInDb = userRepository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(User.class, "id not found in database"));

            // Verify if userName is already taken
            String updateUserDTOUserName = updateUserDTO.getUserName();
            List<User> usernamesInDb = userRepository.findAllByUserName(updateUserDTOUserName);
            if (!usernamesInDb.isEmpty()) {
                User first = usernamesInDb.get(0);
                if (!Objects.equals(first.getId(), id)) {
                    throw new BaseNotFoundException(User.class, "username is already taken");
                }
            }

            // Verify the ProfileId array passed in body
            boolean updateProfiles = false;
            Optional<Integer[]> userProfilesToUpdate = Optional.ofNullable(updateUserDTO.getIdProfiles());
            List<Profile> profilesToSet = new ArrayList<>();
            if (userProfilesToUpdate.isPresent()) {
                for (Integer profileId : userProfilesToUpdate.get()) {
                    Profile profile = profileRepository.findById(profileId)
                            .orElseThrow(
                                    () -> new BaseNotFoundException(
                                            Profile.class, "provided id: " + profileId + " not found in database")
                            );
                    updateProfiles = true;
                    profilesToSet.add(profile);
                }
            }

            Map<String, Object> body = new HashMap<>();
//            body.put("updateProfiles", updateProfiles);

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
                List<UserProfile> updatedUserProfiles = userProfileRepository.findAllByIdUser(userInDb);
                List<Integer> updatedUserProfilesIds = updatedUserProfiles.stream()
                        .map(UserProfile::getId)
                        .toList();
//                body.put("updatedUserProfilesIds", updatedUserProfilesIds);
            }

            UserDTO currentUserData = Utils.convertToDTO(userInDb, UserDTO.class);
//            body.put("currentUserData", currentUserData);

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
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            User userToDelete = userRepository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(User.class, "id was not found in database"));

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
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
        newUser.setUserName(newUserDTO.getUserName());
        newUser.setLastLoginTime(null);
        newUser.setRecoveryToken(null);
        newUser.setCredentialsExpired(false);
        newUser.setCreationDate(new Date());

        return newUser;
    }

    // Method to add New UserProfile row
    private UserProfile addNewUserProfileRow(User user, Profile profile) {
        UserProfile userProfile = new UserProfile();
        userProfile.setIdUser(user);
        userProfile.setIdProfile(profile);
        return userProfileRepository.save(userProfile);
    }

    // Method to add New UserApplication row
    private UserApplication addNewUserApplicationRow(User user, Application application) {
        UserApplication userApplication = new UserApplication();
        userApplication.setIdUser(user);
        userApplication.setIdApplication(application);
        return userApplicationRepository.save(userApplication);
    }
}

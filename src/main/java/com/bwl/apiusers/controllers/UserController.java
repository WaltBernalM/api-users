package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.UserModelAssembler;
import com.bwl.apiusers.dtos.NewUserDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final NewUserDTO newUserDTO;
    private final UserRepository userRepository;
    private final UserModelAssembler assembler;
    private final ProfileRepository profileRepository;
    private final ApplicationRepository applicationRepository;
    private final UserProfileRepository userProfileRepository;
    private  final UserApplicationRepository userApplicationRepository;

    public UserController(
            NewUserDTO newUserDTO,
            UserRepository userRepository,
            UserModelAssembler assembler,
            ProfileRepository profileRepository,
            ApplicationRepository applicationRepository,
            UserProfileRepository userProfileRepository,
            UserApplicationRepository userApplicationRepository) {
        this.newUserDTO = newUserDTO;
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
            String newUserDTOEmail = newUserDTO.getEmail();
            Optional<User> userInDb = userRepository.findOneByEmail(newUserDTOEmail);

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

    private UserProfile addNewUserProfileRow(User user, Profile profile) {
        UserProfile userProfile = new UserProfile();
        userProfile.setIdUser(user);
        userProfile.setIdProfile(profile);
        return userProfileRepository.save(userProfile);
    }

    private UserApplication addNewUserApplicationRow(User user, Application application) {
        UserApplication userApplication = new UserApplication();
        userApplication.setIdUser(user);
        userApplication.setIdApplication(application);
        return userApplicationRepository.save(userApplication);
    }
}

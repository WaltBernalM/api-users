package com.bwl.apiusers.security;

import com.bwl.apiusers.exceptions.ApplicationNotFoundException;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final UserApplicationRepository userApplicationRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProfileRepository profileRepository;
    private final ProfilePermissionRepository profilePermissionRepository;

    private static final ThreadLocal<Integer> idApplicationThreadLocal = new ThreadLocal<>();

    public UserDetailServiceImpl(
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            UserApplicationRepository userApplicationRepository,
            UserProfileRepository userProfileRepository,
            ProfileRepository profileRepository,
            ProfilePermissionRepository profilePermissionRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.userApplicationRepository = userApplicationRepository;
        this.userProfileRepository = userProfileRepository;
        this.profileRepository = profileRepository;
        this.profilePermissionRepository = profilePermissionRepository;
    }

    public static void setIdApplication(Integer idApplication) {
        idApplicationThreadLocal.set(idApplication);
    }

    public static void clearIdApplication() {
        idApplicationThreadLocal.remove();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Integer idApplication = idApplicationThreadLocal.get();
        if (idApplication == null) {
            throw new IllegalStateException("Application ID not set");
        }

        Application application = applicationRepository.findById(idApplication)
                .orElseThrow(() -> new ApplicationNotFoundException(idApplication));

        User user = userRepository
                .findOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        Optional<UserApplication> userApplication = userApplicationRepository.findOneByIdUserAndIdApplication(user, application);
        if (userApplication.isEmpty()) {
            throw new IllegalStateException("No relationship between Application and User");
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // Block to find Profiles related by Application and User
        Application appAssignedToUser = userApplication.get().getIdApplication();
        System.out.println(appAssignedToUser);
        List<Profile> allProfilesRelatedToApp = profileRepository.findAllByIdApplication(appAssignedToUser);
        System.out.println(allProfilesRelatedToApp);
        List<Profile> profilesCommonByAppAndUser = userProfileRepository.findAll()
                .stream()
                .filter(userProfile -> userProfile.getIdUser().equals(user))
                .filter(userProfile -> allProfilesRelatedToApp.contains(userProfile.getIdProfile()))
                .map(UserProfile::getIdProfile)
                .toList();
        System.out.println(profilesCommonByAppAndUser);
        List<String> profileKeycodesCommonByAppAndUser = profilesCommonByAppAndUser
                .stream()
                .map(Profile::getKeycode)
                .toList();
        userDetails.setProfileKeycodes(profileKeycodesCommonByAppAndUser);

        // get profileKeycodes assigned to User
//        List<Integer> userProfileIds = userProfileRepository.findAllByIdUser(user)
//                .stream()
//                .map(row -> row.getIdProfile().getId())
//                .toList();
//        List<String> userProfileKeycodes = new ArrayList<>();
//        for(Integer profileId : userProfileIds) {
//            Optional<Profile> profile = profileRepository.findById(profileId);
//            profile.ifPresent(value -> userProfileKeycodes.add(value.getKeycode()));
//        }
//        userDetails.setProfileKeycodes(userProfileKeycodes);

//        List<Permission> profilePermissions = new ArrayList<>();
        Map<String, Object> main = new LinkedHashMap<>();
        for (Profile profile : profilesCommonByAppAndUser) {
            Map<String, Object> permissionByProfile = new LinkedHashMap<>();
            List<ProfilePermission> profilePermissionsRelated = profilePermissionRepository.findAllByIdProfile(profile);
            for(ProfilePermission profilePermission: profilePermissionsRelated) {
                Permission permission = profilePermission.getIdPermission();
                permissionByProfile.put(
                        "idPermissionKeyCode_" + permission.getId(),
                        permission.getKeycode()
                );
            }
            main.put(profile.getKeycode(), permissionByProfile);
        }
        userDetails.setProfilePermissions(main);


        userDetails.setIdApplication(idApplication);
        clearIdApplication();
        return userDetails;
    }
}

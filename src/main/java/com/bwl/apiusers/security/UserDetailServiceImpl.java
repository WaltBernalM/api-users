package com.bwl.apiusers.security;

import com.bwl.apiusers.exceptions.ApplicationNotFoundException;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        // Block to find Profiles related by Application and User
        Application appAssignedToUser = userApplication.get().getIdApplication();
        List<Profile> allProfilesRelatedToApp = profileRepository.findAllByIdApplication(appAssignedToUser);
        List<Profile> profilesCommonByAppAndUser = userProfileRepository.findAll()
                .stream()
                .filter(userProfile -> userProfile.getIdUser().equals(user))
                .filter(userProfile -> allProfilesRelatedToApp.contains(userProfile.getIdProfile()))
                .map(UserProfile::getIdProfile)
                .toList();
        List<String> profileKeycodesCommonByAppAndUser = profilesCommonByAppAndUser
                .stream()
                .map(Profile::getKeycode)
                .toList();

        Map<String, Object> main = new LinkedHashMap<>();
        for (Profile profile : profilesCommonByAppAndUser) {
            List<ProfilePermission> profilePermissionsRelated = profilePermissionRepository.findAllByIdProfile(profile);
            Map<String, Object> permissionByProfile = new LinkedHashMap<>();
            for(ProfilePermission profilePermission: profilePermissionsRelated) {
                Permission permission = profilePermission.getIdPermission();
                permissionByProfile.put(
                        "permissionKeyCode_" + permission.getId(),
                        permission.getKeycode()
                );
            }
            main.put(profile.getKeycode(), permissionByProfile);
        }

        List<GrantedAuthority> userAuthorities = getUserAuthorities(main);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        userDetails.setProfileKeycodes(profileKeycodesCommonByAppAndUser);
        userDetails.setProfilePermissions(main);
        userDetails.setIdApplication(idApplication);
        userDetails.setAuthorities(userAuthorities);

        System.out.println("user authorities: " + userDetails.getAuthorities());
        clearIdApplication();
        return userDetails;
    }

    private List<GrantedAuthority> getUserAuthorities(Map<String, Object> main) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        main.forEach((profileKeycode, permissions) -> {
            if(permissions instanceof Map) {
                ((Map<?,?>) permissions).forEach((permissionKey, permissionValue) -> {
                    String authority = (String) permissionValue;
                    SimpleGrantedAuthority simpleAuthority = new SimpleGrantedAuthority(authority);
                    if(!authorities.contains(simpleAuthority)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + permissionValue));
                    }
                });
            }
        });
        return authorities;
    }
}

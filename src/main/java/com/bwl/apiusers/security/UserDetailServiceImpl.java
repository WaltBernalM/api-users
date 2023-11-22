package com.bwl.apiusers.security;

import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProfileRepository profileRepository;
    private final PermissionRepository permissionRepository;

    private static final ThreadLocal<Integer> idApplicationThreadLocal = new ThreadLocal<>();

    public UserDetailServiceImpl(
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            UserProfileRepository userProfileRepository,
            ProfileRepository profileRepository,
            PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.userProfileRepository = userProfileRepository;
        this.profileRepository = profileRepository;
        this.permissionRepository = permissionRepository;
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

        User user = userRepository
                .findOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // get profileKeycodes assigned to User
        List<Integer> userProfileIds = userProfileRepository.findAllByIdUser(user)
                .stream()
                .map(row -> row.getIdProfile().getId())
                .toList();
        List<String> userProfileKeycodes = new ArrayList<>();
        for(Integer profileId : userProfileIds) {
            Optional<Profile> profile = profileRepository.findById(profileId);
            profile.ifPresent(value -> userProfileKeycodes.add(value.getKeycode()));
        }
        userDetails.setProfileKeycodes(userProfileKeycodes);

        userDetails.setIdApplication(idApplication);

        List<ProfilePermission> profilePermissions;

        clearIdApplication();
        return userDetails;
    }
}

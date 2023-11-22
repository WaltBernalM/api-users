package com.bwl.apiusers.security;

import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.PermissionRepository;
import com.bwl.apiusers.repositories.ProfileRepository;
import com.bwl.apiusers.repositories.UserProfileRepository;
import com.bwl.apiusers.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProfileRepository profileRepository;
    private final PermissionRepository permissionRepository;

    public UserDetailServiceImpl(UserRepository userRepository, UserProfileRepository userProfileRepository, ProfileRepository profileRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.profileRepository = profileRepository;
        this.permissionRepository = permissionRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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

        List<ProfilePermission> profilePermissions;

        return userDetails;
    }
}

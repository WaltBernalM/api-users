package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Profile;
import com.bwl.apiusers.models.ProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfilePermissionRepository extends JpaRepository<ProfilePermission, Integer> {
    List<ProfilePermission> findAllByIdProfile(Profile idProfile);
}

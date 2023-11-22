package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.UserProfile;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends UserComposedRepository<UserProfile> {
}

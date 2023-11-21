package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
//    List<UserProfile> findAllByIdUser(User user);
//}
public interface UserProfileRepository extends UserComposedRepository<UserProfile> {
}

package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.User;
import com.bwl.apiusers.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    List<UserProfile> findAllByIdUser(User user);
}

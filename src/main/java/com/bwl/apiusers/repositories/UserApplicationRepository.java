package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.models.User;
import com.bwl.apiusers.models.UserApplication;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserApplicationRepository extends UserComposedRepository<UserApplication>{
    Optional<UserApplication> findOneByIdUserAndIdApplication(User user, Application application);
}
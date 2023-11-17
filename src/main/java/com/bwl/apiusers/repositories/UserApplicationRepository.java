package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.User;
import com.bwl.apiusers.models.UserApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//public interface UserApplicationRepository extends JpaRepository<UserApplication, Integer> {
//    List<UserApplication> findAllByIdUser(User user);
//}
public interface UserApplicationRepository extends UserComposedRepository<UserApplication>{
}
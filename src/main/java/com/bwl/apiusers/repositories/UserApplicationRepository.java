package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.UserApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserApplicationRepository extends JpaRepository<UserApplication, Integer> {
}

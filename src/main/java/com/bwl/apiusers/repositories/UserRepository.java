package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}

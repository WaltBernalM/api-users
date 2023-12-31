package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {
    Optional<User> findOneByEmail(String email);
    Optional<User> findOneByUsername(String username);
    List<User> findAllByUsername(String username);
}

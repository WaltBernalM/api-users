package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvalidTokenRepository extends JpaRepository<InvalidToken, Integer> {
    Optional<InvalidToken> findOneByToken(String token);
}

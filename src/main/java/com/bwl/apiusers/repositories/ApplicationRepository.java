package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
}

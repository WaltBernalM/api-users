package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends BaseRepository<Application> {
//    Page<Application> findByNameContaining(String name, Pageable pageable);
//    List<Application> findByNameContaining(String name, Sort sort);
}

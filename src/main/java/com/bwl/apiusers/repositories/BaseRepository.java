package com.bwl.apiusers.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Integer> {
    Page<T> findByNameContaining(String name, Pageable pageable);
    List<T> findByNameContaining(String name, Sort sort);
}

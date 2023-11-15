package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.models.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Page<Permission> findByNameContaining(String name, Pageable pageable);
    List<Permission> findByNameContaining(String name, Sort sort);
}

package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends  BaseRepository<Project> {
    Optional<Project> findOneByKeycode(String keycode);
    List<Project> findAllByKeycode(String keycode);
}

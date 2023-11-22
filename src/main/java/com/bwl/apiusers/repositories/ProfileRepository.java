package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.models.Profile;

import java.util.List;

public interface ProfileRepository extends BaseRepository<Profile> {
    List<Profile> findAllByIdApplication (Application idApplication);
}

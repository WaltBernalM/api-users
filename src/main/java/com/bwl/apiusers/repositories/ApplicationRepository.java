package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Application;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends BaseRepository<Application> {

}

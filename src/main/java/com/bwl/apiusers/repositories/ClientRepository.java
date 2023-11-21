package com.bwl.apiusers.repositories;

import com.bwl.apiusers.models.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends BaseRepository<Client>{
    Optional<Client> findOneByName(String name);
    Optional<Client> findOneByShortName(String shortName);
    List<Client> findAllByName(String name);
    List<Client> findAllByShortName(String shortName);
}

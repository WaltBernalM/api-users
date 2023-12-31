package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.ProfileModelAssembler;
import com.bwl.apiusers.dtos.ProfileDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.Profile;
import com.bwl.apiusers.repositories.ProfileRepository;
import com.bwl.apiusers.utils.Utils;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ProfileService extends GenericReadService<Profile, ProfileDTO, ProfileRepository, ProfileModelAssembler> {
    public ProfileService(ProfileRepository repository, ProfileModelAssembler assembler) {
        super(repository, assembler, Profile.class, ProfileDTO.class);
    }
}

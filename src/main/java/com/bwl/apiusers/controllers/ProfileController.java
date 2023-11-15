package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.ProfileDTO;
import com.bwl.apiusers.assemblers.ProfileModelAssembler;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.models.Profile;
import com.bwl.apiusers.repositories.ProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/profiles")
public class ProfileController extends BaseController<Profile, ProfileRepository, ProfileModelAssembler> {
    ProfileController(ProfileRepository repository, ProfileModelAssembler assembler) {
        super(repository, assembler, Profile.class);
    }
}

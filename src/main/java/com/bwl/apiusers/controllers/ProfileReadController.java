package com.bwl.apiusers.controllers;

import com.bwl.apiusers.services.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileReadController implements GenericReadController {
    private final ProfileService service;

    public ProfileReadController(ProfileService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<?> one(Integer id) {
        return service.one(id);
    }

    @Override
    public ResponseEntity<?> all(String name, int page, int size, String[] sort) {
        return service.all(name, page, size, sort);
    }
}

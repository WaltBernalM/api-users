package com.bwl.apiusers.controllers;

import com.bwl.apiusers.services.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController  implements GenericController {

    private final PermissionService service;
    public PermissionController(PermissionService service) {
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

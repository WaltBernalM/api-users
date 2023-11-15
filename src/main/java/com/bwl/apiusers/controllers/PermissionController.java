package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.PermissionModelAssembler;
import com.bwl.apiusers.exceptions.PermissionNotFoundException;
import com.bwl.apiusers.models.Permission;
import com.bwl.apiusers.repositories.PermissionRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/permissions")
public class PermissionController {
    private final PermissionRepository repository;
    private final PermissionModelAssembler assembler;

    public PermissionController(PermissionRepository repository, PermissionModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("{id}")
    public EntityModel<Permission> one (@PathVariable Integer id) {
        Permission permission = repository.findById(id).orElseThrow(() -> new PermissionNotFoundException(id));
        return assembler.toModel(permission);
    }
}

package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.PermissionModelAssembler;
import com.bwl.apiusers.repositories.PermissionRepository;
import org.springframework.web.bind.annotation.GetMapping;
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

//    @GetMapping("{id}")

}

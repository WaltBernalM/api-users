package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.PermissionModelAssembler;
import com.bwl.apiusers.dtos.DTO;
import com.bwl.apiusers.models.Permission;
import com.bwl.apiusers.repositories.PermissionRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/permissions")
public class PermissionController  extends BaseController<Permission, DTO, PermissionRepository, PermissionModelAssembler>{
    public PermissionController(PermissionRepository repository, PermissionModelAssembler assembler) {
        super(repository, assembler, Permission.class, DTO.class);
    }
}

package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.PermissionModelAssembler;
import com.bwl.apiusers.dtos.DTO;
import com.bwl.apiusers.models.Permission;
import com.bwl.apiusers.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionService extends GenericReadService<Permission, DTO, PermissionRepository, PermissionModelAssembler> {
    public PermissionService(PermissionRepository repository, PermissionModelAssembler assembler) {
        super(repository, assembler, Permission.class, DTO.class);
    }
}

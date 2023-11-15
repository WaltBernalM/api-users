package com.bwl.apiusers.assemblers;

import com.bwl.apiusers.models.Permission;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class PermissionModelAssembler implements RepresentationModelAssembler<Permission, EntityModel<Permission>>{
    @Override
    public EntityModel<Permission> toModel(Permission permission) {
        return EntityModel.of(permission);
    }
}

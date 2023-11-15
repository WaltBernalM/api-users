package com.bwl.apiusers.assemblers;

import com.bwl.apiusers.models.Permission;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class PermissionModelAssembler extends BaseModelAssembler<Permission> {

}

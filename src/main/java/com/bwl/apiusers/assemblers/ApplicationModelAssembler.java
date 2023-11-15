package com.bwl.apiusers.assemblers;

import com.bwl.apiusers.controllers.ApplicationController;
import com.bwl.apiusers.models.Application;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ApplicationModelAssembler implements RepresentationModelAssembler<Application, EntityModel<Application>> {
    @Override
    public EntityModel<Application> toModel(Application application) {
        return EntityModel.of(application);
    }
}

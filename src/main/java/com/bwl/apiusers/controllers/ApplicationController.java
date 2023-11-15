package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.ApplicationModelAssembler;
import com.bwl.apiusers.exceptions.ApplicationNotFoundException;
import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.repositories.ApplicationRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationRepository repository;
    private final ApplicationModelAssembler assembler;

    public ApplicationController(ApplicationRepository repository, ApplicationModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/applications/{id}")
    public EntityModel<Application> one(@PathVariable Integer id) {
        Application app = repository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));

        return assembler.toModel(app);
    }

    @GetMapping("/applications")
    public CollectionModel<EntityModel<Application>> all() {
        List<EntityModel<Application>> applications = repository.findAll().stream()
                .map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(applications, linkTo(methodOn(ApplicationController.class).all()).withSelfRel());
    }
}

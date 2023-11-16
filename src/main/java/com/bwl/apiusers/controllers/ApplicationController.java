package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.ApplicationModelAssembler;
import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.dtos.DTO;
import com.bwl.apiusers.repositories.ApplicationRepository;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController extends  BaseController<Application, DTO, ApplicationRepository, ApplicationModelAssembler> {
    public ApplicationController(ApplicationRepository repository, ApplicationModelAssembler assembler) {
        super(repository, assembler, Application.class, DTO.class);
    }
}

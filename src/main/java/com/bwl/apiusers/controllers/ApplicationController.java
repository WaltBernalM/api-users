package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.ApplicationModelAssembler;
import com.bwl.apiusers.exceptions.ApplicationNotFoundException;
import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.repositories.ApplicationRepository;
import com.bwl.apiusers.utils.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController extends  BaseController<Application, ApplicationRepository, ApplicationModelAssembler> {
    public ApplicationController(ApplicationRepository repository, ApplicationModelAssembler assembler) {
        super(repository, assembler, Application.class);
    }
}

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
public class ApplicationController {
    private final ApplicationRepository repository;
    private final ApplicationModelAssembler assembler;

    public ApplicationController(ApplicationRepository repository, ApplicationModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

//    private Sort.Direction getSortDirection(String direction) {
//        if (direction == null || direction.isEmpty() || direction.equalsIgnoreCase("asc")) {
//            return Sort.Direction.ASC;
//        } else if (direction.equalsIgnoreCase("desc")) {
//            return Sort.Direction.DESC;
//        } else {
//            throw new IllegalArgumentException("Invalid sort direction: " + direction);
//        }
//    }

    @GetMapping("/{id}")
    public EntityModel<Application> one(@PathVariable Integer id) {
        Application app = repository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));

        return assembler.toModel(app);
    }

//    @GetMapping("/applications")
//    public CollectionModel<EntityModel<Application>> all() {
//        List<EntityModel<Application>> applications = repository.findAll().stream()
//                .map(assembler::toModel).collect(Collectors.toList());
//        return CollectionModel.of(applications, linkTo(methodOn(ApplicationController.class).all()).withSelfRel());
//    }
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> all(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        try {
            List<Order> orders = new ArrayList<Order>();

            if (sort[0].contains(",")) {
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(SortDirection.get(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Order(SortDirection.get(sort[1]), sort[0]));
            }

            List<Application> apps = new ArrayList<Application>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Application> appTuts;
            if (name == null)
                appTuts = repository.findAll(pagingSort);
            else
                appTuts = repository.findByNameContaining(name, pagingSort);

            apps = appTuts.getContent();

            if (apps.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            if(page == 0 && size == 2 && sort[0].equals("id") && sort[1].equals("asc")) {
                Map<String, Object> all = new HashMap<>();
                all.put("applications", apps);
                return new ResponseEntity<>(all, HttpStatus.OK);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("applications", apps);
            response.put("currentPage", appTuts.getNumber());
            response.put("totalItems", appTuts.getTotalElements());
            response.put("totalPages", appTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

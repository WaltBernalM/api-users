package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.BaseModelAssembler;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.repositories.BaseRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseController<T,R extends BaseRepository<T>, M extends BaseModelAssembler<T>> {
    private final R repository;
    private final M assembler;
    private final Class<T> entityClass;

    public BaseController(R repository, M assembler, Class<T> entityClass) {
        this.repository = repository;
        this.assembler = assembler;
        this.entityClass = entityClass;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Integer id) {
        try {
            T entity = repository.findById(id).orElseThrow(() -> new BaseNotFoundException(entityClass, id));
            EntityModel<T> entityModel = assembler.toModel(entity);
            return ResponseEntity.ok(entityModel);
        } catch (Exception e) {
            ErrorResponse errorresponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorresponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> all (
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + Integer.MAX_VALUE) int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        try {
            List<Sort.Order> orders = new ArrayList<>();

            if(sort[0].contains(",")) {
                for(String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(Sort.Direction.fromString(_sort[1]), sort[0]));
                }
            } else {
                orders.add(new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0]));
            }
            List<T> entities = new ArrayList<>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<T> entityPage;
            if(name == null) {
                entityPage = repository.findAll(pagingSort);
            } else {
                entityPage = repository.findByNameContaining(name, pagingSort);
            }
            entities = entityPage.getContent();

            if (entities.isEmpty()) {
                throw new BaseNotFoundException(entityClass);
            }

            if (page == 0 && size == Integer.MAX_VALUE && sort[0].equals("id") && sort[1].equals("asc")) {
                Map<String, Object> all = new HashMap<>();
                all.put("data", entities);
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("data", entities);
            response.put("currentPage", entityPage.getNumber());
            response.put("totalItems", entityPage.getTotalElements());
            response.put("totalPages", entityPage.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


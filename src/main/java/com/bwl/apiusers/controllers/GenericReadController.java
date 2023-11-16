package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.BaseModelAssembler;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.repositories.BaseRepository;
import com.bwl.apiusers.utils.Utils;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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

// Class T represents  the Model, Class D represents the DTO model, R represents Repository and M model assembler
public abstract class GenericReadController<T, D, R extends BaseRepository<T>, M extends BaseModelAssembler<T>> {
    @Getter
    private final R repository;

    @Getter
    private final M assembler;

    private final Class<T> entityClass;
    private final Class<D> dtoEntityClass;

    public GenericReadController(R repository, M assembler, Class<T> entityClass, Class<D> dtoEntityClass) {
        this.repository = repository;
        this.assembler = assembler;
        this.entityClass = entityClass;
        this.dtoEntityClass = dtoEntityClass;
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
            // Declaration of an empty list of Order
            List<Order> orders = new ArrayList<>();

            // Decomposition of the ?sort= query param and assignation of type Order to orders List
            if(sort[0].contains(",")) {
                for(String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(Utils.getSortDirection(_sort[1]), sort[0]));
                }
            } else {
                orders.add(new Order(Utils.getSortDirection(sort[1]), sort[0]));
            }

            // Declaration of entity new list of type T
            List<T> entities = new ArrayList<>();

            // Declaration of paging sort by default or user query inputs
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            // Declaration of pages for repository results by paging sort
            Page<T> entityPage = Utils.generateEntityPage(name, pagingSort, repository);

            // Assignation of entityPage (result of repository) to entities List with T parameters
            entities = entityPage.getContent();

            // Throw exception if the entities List is empty
            if (entities.isEmpty()) {
                throw new BaseNotFoundException(entityClass);
            }

            // Initialization of response content
            Map<String, Object>  response = new HashMap<>();

            // conditional DTO parsing
            Utils.parseResponseData(entities, dtoEntityClass, response);

            // If all query params are as default, return full list
            if (page == 0 && size == Integer.MAX_VALUE && sort[0].equals("id") && sort[1].equals("asc")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            // Body response construction as Map
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


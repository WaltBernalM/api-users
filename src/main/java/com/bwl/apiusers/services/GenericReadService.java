package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.BaseModelAssembler;
import com.bwl.apiusers.assemblers.DTOModelAssembler;
import com.bwl.apiusers.dtos.UserDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.repositories.BaseRepository;
import com.bwl.apiusers.utils.Utils;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericReadService <T, D, R extends BaseRepository<T>, M extends DTOModelAssembler<D, T>> {
    @Getter
    private final R repository;

    @Getter
    private final M assembler;

    private final Class<T> entityClass;
    private final Class<D> dtoEntityClass;

    public GenericReadService(R repository, M assembler, Class<T> entityClass, Class<D> dtoEntityClass) {
        this.repository = repository;
        this.assembler = assembler;
        this.entityClass = entityClass;
        this.dtoEntityClass = dtoEntityClass;
    }

    public ResponseEntity<?> one( Integer id) {
        try {
            T entity = repository.findById(id).orElseThrow(() -> new BaseNotFoundException(entityClass, id));
            D dto = Utils.convertToDTO(entity, dtoEntityClass);
            EntityModel<D> entityModel = assembler.toModel(dto);

            return ResponseEntity.ok(entityModel);

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> all (
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + Integer.MAX_VALUE) int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        try {
            List<Sort.Order> orders = new ArrayList<>();

            getOrders(sort, orders);

            List<T> entities = new ArrayList<>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
            Page<T> entityPage = Utils.generateEntityPage(name, pagingSort, repository);
            entities = entityPage.getContent();
            if (entities.isEmpty()) {
                throw new BaseNotFoundException(entityClass);
            }
            Map<String, Object> response = new HashMap<>();
            Utils.parseResponseData(entities, dtoEntityClass, response);
            if (page == 0 && size == Integer.MAX_VALUE && sort[0].equals("id") && sort[1].equals("asc")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

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

    private void getOrders(String[] sort, List<Sort.Order> orders) {
        if(sort[0].contains(",")) {
            for(String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(Utils.getSortDirection(_sort[1]), sort[0]));
            }
            return;
        }
        orders.add(new Sort.Order(Utils.getSortDirection(sort[1]), sort[0]));
    }
}

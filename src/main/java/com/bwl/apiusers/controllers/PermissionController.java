package com.bwl.apiusers.controllers;

import com.bwl.apiusers.assemblers.PermissionModelAssembler;
import com.bwl.apiusers.models.Permission;
import com.bwl.apiusers.repositories.PermissionRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/permissions")
public class PermissionController  extends BaseController<Permission, PermissionRepository, PermissionModelAssembler>{
    public PermissionController(PermissionRepository repository, PermissionModelAssembler assembler) {
        super(repository, assembler, Permission.class);
    }

//    private final PermissionRepository repository;
//    private final PermissionModelAssembler assembler;
//
//    public PermissionController(PermissionRepository repository, PermissionModelAssembler assembler) {
//        this.repository = repository;
//        this.assembler = assembler;
//    }
//
//    @GetMapping("{id}")
//    public EntityModel<Permission> one (@PathVariable Integer id) {
//        Permission permission = repository.findById(id).orElseThrow(() -> new PermissionNotFoundException(id));
//        return assembler.toModel(permission);
//    }
//
//    @GetMapping("")
//    public ResponseEntity<Map<String, Object>> all(
//            @RequestParam(required = false) String name,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "2") int size,
//            @RequestParam(defaultValue = "id,asc") String[] sort
//    ) {
//        try {
//            List<Sort.Order> orders = new ArrayList<Sort.Order>();
//
//            if (sort[0].contains(",")) {
//                for (String sortOrder : sort) {
//                    String[] _sort = sortOrder.split(",");
//                    orders.add(new Sort.Order(SortDirection.get(_sort[1]), _sort[0]));
//                }
//            } else {
//                // sort=[field, direction]
//                orders.add(new Sort.Order(SortDirection.get(sort[1]), sort[0]));
//            }
//
//            List<Permission> permissions = new ArrayList<Permission>();
//            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
//
//            Page<Permission> permissionTuts;
//            if (name == null)
//                permissionTuts = repository.findAll(pagingSort);
//            else
//                permissionTuts = repository.findByNameContaining(name, pagingSort);
//
//            permissions = permissionTuts.getContent();
//
//            if (permissions.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//
//            if(page == 0 && size == 2 && sort[0].equals("id") && sort[1].equals("asc")) {
//                Map<String, Object> all = new HashMap<>();
//                all.put("permissions", permissions);
//                return new ResponseEntity<>(all, HttpStatus.OK);
//            }
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("permissions", permissions);
//            response.put("currentPage", permissionTuts.getNumber());
//            response.put("totalItems", permissionTuts.getTotalElements());
//            response.put("totalPages", permissionTuts.getTotalPages());
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}

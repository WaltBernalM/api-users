package com.bwl.apiusers.controllers;

import com.bwl.apiusers.dtos.NewUserDTO;
import com.bwl.apiusers.dtos.UpdateUserDTO;
import com.bwl.apiusers.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController implements GenericCRUDController<NewUserDTO, UpdateUserDTO> {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<?> postNew(NewUserDTO newUserDTO) {
        return service.newUserSignup(newUserDTO);
    }

    @Override
    public ResponseEntity<?> updateUser(Integer id, UpdateUserDTO updateUserDTO) {
        return service.updateUser(id, updateUserDTO);
    }

    @Override
    public ResponseEntity<?> deleteUser(Integer id) {
        return service.deleteUser(id);
    }

    @Override
    public ResponseEntity<?> one(Integer id) {
        return service.one(id);
    }

    @Override
    public ResponseEntity<?> all(String name, int page, int size, String[] sort) {
        return service.all(name, page, size, sort);
    }
}

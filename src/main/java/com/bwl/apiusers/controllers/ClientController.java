package com.bwl.apiusers.controllers;

import com.bwl.apiusers.dtos.NewClientDTO;
import com.bwl.apiusers.dtos.UpdateClientDTO;
import com.bwl.apiusers.services.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController implements GenericCRUDController<NewClientDTO, UpdateClientDTO> {
    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<?> postNew(NewClientDTO newClientDTO) {
        return service.newClientSignup(newClientDTO);
    }

    @Override
    public ResponseEntity<?> updateOne(Integer id, UpdateClientDTO updateClientDTO) {
        return service.updateClient(id, updateClientDTO);
    }

    @Override
    public ResponseEntity<?> deleteOne(Integer id) {
        return null;
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

package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.ClientModelAssembler;
import com.bwl.apiusers.dtos.ClientDTO;
import com.bwl.apiusers.dtos.NewClientDTO;
import com.bwl.apiusers.dtos.UpdateClientDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.Client;
import com.bwl.apiusers.repositories.ClientRepository;
import com.bwl.apiusers.utils.Utils;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class ClientService extends  GenericReadService<Client, ClientDTO, ClientRepository, ClientModelAssembler> {
    private final ClientRepository repository;
    private final ClientModelAssembler assembler;

    public ClientService(
            ClientRepository repository,
            ClientModelAssembler assembler
    ){
        super(repository, assembler, Client.class, ClientDTO.class);
        this.repository = repository;
        this.assembler = assembler;
    }

    public ResponseEntity<?> newClientSignup(@RequestBody NewClientDTO newClientDTO) {
        try {
            String newClientDTOName = newClientDTO.getName();
            Optional<Client> clientInDb = repository.findOneByName(newClientDTOName);

            String newClientDTOShortName = newClientDTO.getShortName();
            Optional<Client> clientShortNameInDb = repository.findOneByShortName(newClientDTOShortName);

            if(clientInDb.isEmpty() && clientShortNameInDb.isEmpty()) {
                Client newClient = parseToClient(newClientDTO);
                Client savedClient = repository.save(newClient);

                Map<String, Object> body = new HashMap<>();
                body.put("client", savedClient);

                Map<String, Object> response = new HashMap<>();
                response.put("data", body);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

            throw new BaseNotFoundException(Client.class, "Name or short name already exists in database");
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> one(@PathVariable Integer id) {
        try {
            Client client = repository.findById(id).orElseThrow(() -> new BaseNotFoundException(Client.class, id));
            ClientDTO dto = Utils.convertToDTO(client, ClientDTO.class);
            EntityModel<ClientDTO> entityModel = assembler.toModel(dto);

            return ResponseEntity.ok(entityModel);
        } catch (Exception e) {
            ErrorResponse errorresponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorresponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateClient(@PathVariable Integer id, @RequestBody UpdateClientDTO updateClientDTO) {
        try {
            Client clientInDb = repository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(Client.class, "id not found in database"));

            String updateClientDTOName = updateClientDTO.getName();
            List<Client> clientNamesInDb = repository.findAllByName(updateClientDTOName);
            if (!clientNamesInDb.isEmpty()) {
                Client first = clientNamesInDb.getFirst();
                if (!Objects.equals(first.getId(), id)) {
                    throw new BaseNotFoundException(Client.class, "name already taken");
                }
            }

            String updateClientDTOShortName = updateClientDTO.getShortName();
            List<Client> clientShortNamesInDb = repository.findAllByShortName(updateClientDTOShortName);
            if (!clientShortNamesInDb.isEmpty()) {
                Client first = clientShortNamesInDb.getFirst();
                if (!Objects.equals(first.getId(), id)) {
                    throw new BaseNotFoundException(Client.class, "short name already taken");
                }
            }

            Map<String, Object> body = new HashMap<>();

            ClientDTO currentClientData = Utils.convertToDTO(clientInDb, ClientDTO.class);

            for (Field field : updateClientDTO.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(updateClientDTO);
                if (value != null) {
                    Field clientField = Client.class.getDeclaredField(field.getName());
                    clientField.setAccessible(true);
                    clientField.set(clientInDb, value);
                }
            }

            Client updatedClient = repository.save(clientInDb);
            body.put("client", updatedClient);

            Map<String, Object> response = new HashMap<>();
            response.put("data", body);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
        try {
            Client clientToDelete = repository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(Client.class, "id was not found in database"));



            return null;
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    private Client parseToClient(NewClientDTO newClientDTO) {
        Client newClient = new Client();
        newClient.setName(newClientDTO.getName());
        newClient.setShortName(newClientDTO.getShortName());
        newClient.setEnabled(true);
        return newClient;
    }
}

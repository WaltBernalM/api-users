package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.ClientModelAssembler;
import com.bwl.apiusers.dtos.ClientDTO;
import com.bwl.apiusers.dtos.NewClientDTO;
import com.bwl.apiusers.dtos.UpdateClientDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.Client;
import com.bwl.apiusers.models.Project;
import com.bwl.apiusers.repositories.ClientRepository;
import com.bwl.apiusers.repositories.ProjectRepository;
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
    private final ClientRepository clientRepository;
    private final ClientModelAssembler assembler;
    private final ProjectRepository projectRepository;

    public ClientService(
            ClientRepository clientRepository,
            ClientModelAssembler assembler,
            ProjectRepository projectRepository
    ){
        super(clientRepository, assembler, Client.class, ClientDTO.class);
        this.clientRepository = clientRepository;
        this.assembler = assembler;
        this.projectRepository = projectRepository;
    }

    public ResponseEntity<?> newClientSignup(@RequestBody NewClientDTO newClientDTO) {
        try {
            String newClientDTOName = newClientDTO.getName();
            Optional<Client> clientInDb = clientRepository.findOneByName(newClientDTOName);

            String newClientDTOShortName = newClientDTO.getShortName();
            Optional<Client> clientShortNameInDb = clientRepository.findOneByShortName(newClientDTOShortName);

            if(clientInDb.isEmpty() && clientShortNameInDb.isEmpty()) {
                Client newClient = parseToClient(newClientDTO);
                Client savedClient = clientRepository.save(newClient);

                Map<String, Object> body = new HashMap<>();
                body.put("client", savedClient);

                Map<String, Object> response = new HashMap<>();
                response.put("data", body);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

            throw new BaseNotFoundException(Client.class, "Name or short name already exists in database");
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateClient(@PathVariable Integer id, @RequestBody UpdateClientDTO updateClientDTO) {
        try {
            Client clientInDb = clientRepository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(Client.class, "id not found in database"));

            String updateClientDTOName = updateClientDTO.getName();
            List<Client> clientNamesInDb = clientRepository.findAllByName(updateClientDTOName);
            if (!clientNamesInDb.isEmpty()) {
                Client first = clientNamesInDb.getFirst();
                if (!Objects.equals(first.getId(), id)) {
                    throw new BaseNotFoundException(Client.class, "name already taken registered to another Client id");
                }
            }

            String updateClientDTOShortName = updateClientDTO.getShortName();
            List<Client> clientShortNamesInDb = clientRepository.findAllByShortName(updateClientDTOShortName);
            if (!clientShortNamesInDb.isEmpty()) {
                Client first = clientShortNamesInDb.getFirst();
                if (!Objects.equals(first.getId(), id)) {
                    throw new BaseNotFoundException(Client.class, "short name already registered to another Client id");
                }
            }

            for (Field field : updateClientDTO.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(updateClientDTO);
                if (value != null) {
                    Field clientField = Client.class.getDeclaredField(field.getName());
                    clientField.setAccessible(true);
                    clientField.set(clientInDb, value);
                }
            }

            Client updatedClient = clientRepository.save(clientInDb);
            Map<String, Object> body = new HashMap<>();
            body.put("client", updatedClient);

            Map<String, Object> response = new HashMap<>();
            response.put("data", body);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
        try {
            Client clientToDelete = clientRepository.findById(id)
                    .orElseThrow(() -> new BaseNotFoundException(Client.class, "id was not found in database"));
            List<Project> projectsWithClient = projectRepository.findAllByIdClient(clientToDelete);

            if (!projectsWithClient.isEmpty()) {
                throw new BaseNotFoundException(Client.class, "target cannot be erased, Projects still contains this Client");
            }

            clientRepository.deleteById(id);

            Map<String, Object> body = new HashMap<>();
            ClientDTO clientDeleted = Utils.convertToDTO(clientToDelete, ClientDTO.class);
            body.put("client", clientDeleted);
            Map<String, Object> response = new HashMap<>();
            response.put("data", body);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            if (e instanceof BaseNotFoundException) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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

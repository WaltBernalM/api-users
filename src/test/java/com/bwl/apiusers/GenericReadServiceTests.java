package com.bwl.apiusers;

import com.bwl.apiusers.assemblers.ApplicationModelAssembler;
import com.bwl.apiusers.dtos.ApplicationDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.repositories.ApplicationRepository;
import com.bwl.apiusers.services.GenericReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class GenericReadServiceTests {

    @Mock
    private ApplicationRepository mockRepository;

    @Mock
    private ApplicationModelAssembler mockAssembler;

    @InjectMocks
    private GenericReadService<Application, ApplicationDTO, ApplicationRepository, ApplicationModelAssembler> readService =
            new GenericReadService<>(mockRepository, mockAssembler, Application.class, ApplicationDTO.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        readService = new GenericReadService<>(mockRepository, mockAssembler, Application.class, ApplicationDTO.class);
    }

    @Test
    void testGetRepository() {
        assertEquals(mockRepository, readService.getRepository());
    }

    @Test
    void testGetAssembler() {
        assertEquals(mockAssembler, readService.getAssembler());
    }

    @Test
    public void testGetEntityById() {
        int entityId = 1;
        Optional<Application> mockApp = Optional.of(new Application());
        ApplicationDTO expectedEntity = new ApplicationDTO();

        when(mockRepository.findById(anyInt())).thenReturn(mockApp);

        EntityModel<ApplicationDTO> entityModel = EntityModel.of(new ApplicationDTO());
        when(mockAssembler.toModel(expectedEntity)).thenReturn(entityModel);

        ResponseEntity<?> actualEntity = readService.one(entityId);

        assertNotNull(actualEntity);
        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) actualEntity.getBody();

        assertTrue(actualEntity.getBody() instanceof Map);

        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("data"));
    }

    @Test
    public void  testGetEntityByIdNotFound() {
        int entityId = 1;
        when(mockRepository.findById(anyInt())).thenThrow(new BaseNotFoundException(Class.class, entityId));
        ResponseEntity<?> responseEntity = readService.one(entityId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
    }

    @Test
    public void  testGetEntityByIdServerError() {
        int entityId = 1;
        when(mockRepository.findById(anyInt())).thenThrow(new RuntimeException("Test Exception"));
        ResponseEntity<?> responseEntity = readService.one(entityId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
    }

    @Test
    public void testGetAllEntitiesNonPaged() {
        List<Application> list = new ArrayList<>();
        Application app = new Application();
        app.setKeycode("MOCK");
        app.setDescription("MOCK");
        app.setId(1);
        app.setName("Mock");
        list.add(app);

        when(mockRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(list));

        ResponseEntity<?> responseEntity = readService.all(null, 0, Integer.MAX_VALUE, new String[]{"id","asc"});

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof Map);
    }

    @Test
    public void testGetAllEntitiesPaged() {
        List<Application> list = new ArrayList<>();
        Application app = new Application();
        app.setKeycode("MOCK");
        app.setDescription("MOCK");
        app.setId(1);
        app.setName("Mock");
        list.add(app);

        when(mockRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(list));
        ResponseEntity<?> responseEntity = readService.all(null, 0, Integer.MAX_VALUE, new String[]{"id,asc"});

        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof Map);

        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("currentPage"));
        assertTrue(responseBody.containsKey("totalItems"));
        assertTrue(responseBody.containsKey("totalPages"));
        assertTrue(responseBody.containsKey("data"));
    }

    @Test
    void testGetAllReturnExceptionError() {
        when(mockRepository.findAll()).thenThrow(new RuntimeException("Test Exception"));
        ResponseEntity<?> responseEntity = readService.all(null, 0, Integer.MAX_VALUE, new String[]{"id,asc"});

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
    }

    @Test
    void testGetAllReturnNotFoundError() {
        when(mockRepository.findAll()).thenThrow(new BaseNotFoundException(Class.class, 1));
        when(readService.all(null, 0, Integer.MAX_VALUE, new String[]{"id,asc"}))
                .thenThrow(new BaseNotFoundException(Class.class, anyInt()));

        ResponseEntity<?> responseEntity = readService.all(null, 0, Integer.MAX_VALUE, new String[]{"id,asc"});

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
    }
}

package com.bwl.apiusers;

import com.bwl.apiusers.assemblers.ProfileModelAssembler;
import com.bwl.apiusers.dtos.ProfileDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.exceptions.ErrorResponse;
import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.models.Profile;
import com.bwl.apiusers.repositories.ProfileRepository;
import com.bwl.apiusers.services.ProfileService;
import com.bwl.apiusers.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProfileServiceTests {
    @Mock
    private ProfileRepository mockRepository;

    @Mock
    private ProfileModelAssembler mockAssembler;

    @InjectMocks
    private ProfileService profileService =
            new ProfileService(mockRepository, mockAssembler);

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        profileService = new ProfileService(mockRepository, mockAssembler);
    }

    @Test
    void testGetRepository() {
        assertEquals(mockRepository, profileService.getRepository());
    }

    @Test
    void testGetAssembler() {
        assertEquals(mockAssembler, profileService.getAssembler());
    }

    @Test
    public void testGetOne() {
        int entityId = 1;
        Profile profile = new Profile();
        profile.setDescription("mock");
        profile.setId(1);
        profile.setKeycode("MOCK");
        profile.setIdApplication(new Application());
        profile.setName("mock");

        ProfileDTO dto = new ProfileDTO();
        dto.setKeycode(profile.getKeycode());
        dto.setName(profile.getName());
        dto.setDescription(profile.getDescription());
        dto.setId(profile.getId());

        when(mockRepository.findById(anyInt())).thenReturn(Optional.of(profile));

        Mockito.mockStatic(Utils.class);
        when(Utils.convertToDTO(profile, ProfileDTO.class)).thenReturn(dto);

        when(mockAssembler.toModel(any())).thenCallRealMethod();

        ResponseEntity<?> actualEntity = profileService.one(entityId);

        assertNotNull(actualEntity);
        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());
        assertEquals(EntityModel.of(dto), actualEntity.getBody());
    }

    @Test
    public void  testGetOneNotFound() {
        int entityId = 1;
        when(mockRepository.findById(anyInt())).thenThrow(new BaseNotFoundException(Class.class, entityId));
        ResponseEntity<?> responseEntity = profileService.one(entityId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody());

        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
    }

    @Test
    public void  testGetOneServerError() {
        int entityId = 1;
        when(mockRepository.findById(anyInt())).thenThrow(new RuntimeException("Test Exception"));
        ResponseEntity<?> responseEntity = profileService.one(entityId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
    }
}

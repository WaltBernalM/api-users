package com.bwl.apiusers;

import com.bwl.apiusers.assemblers.PermissionModelAssembler;
import com.bwl.apiusers.repositories.PermissionRepository;
import com.bwl.apiusers.services.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class PermissionServiceTests {
    private PermissionRepository mockRepository;
    private PermissionModelAssembler mockAssembler;
    private PermissionService applicationService;

    @BeforeEach
    void setUp() {
        mockRepository = mock(PermissionRepository.class);
        mockAssembler = mock(PermissionModelAssembler.class);
        applicationService = new PermissionService(mockRepository, mockAssembler);
    }

    @Test
    void testGetRepository() {
        assertEquals(mockRepository, applicationService.getRepository());
    }

    @Test
    void testGetAssembler() {
        assertEquals(mockAssembler, applicationService.getAssembler());
    }
}

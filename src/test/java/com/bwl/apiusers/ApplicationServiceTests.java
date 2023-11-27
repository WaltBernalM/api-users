package com.bwl.apiusers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bwl.apiusers.assemblers.ApplicationModelAssembler;
import com.bwl.apiusers.repositories.ApplicationRepository;
import com.bwl.apiusers.services.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApplicationServiceTests {
    private ApplicationRepository mockRepository;
    private ApplicationModelAssembler mockAssembler;
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        mockRepository = mock(ApplicationRepository.class);
        mockAssembler = mock(ApplicationModelAssembler.class);
        applicationService = new ApplicationService(mockRepository, mockAssembler);
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

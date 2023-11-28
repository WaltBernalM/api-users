package com.bwl.apiusers;

import com.bwl.apiusers.dtos.DTO;
import com.bwl.apiusers.dtos.ProjectDTO;
import com.bwl.apiusers.exceptions.BaseNotFoundException;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.BaseRepository;
import com.bwl.apiusers.repositories.UserComposedRepository;
import com.bwl.apiusers.utils.Utils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UtilsTests {

    @Test
    public void testGetSortDirectionAsc() {
        Sort.Direction direction = Utils.getSortDirection("asc");
        assertEquals(Sort.Direction.ASC, direction);
    }

    @Test
    public void testGetSortDirectionDesc() {
        Sort.Direction direction = Utils.getSortDirection("desc");
        assertEquals(Sort.Direction.DESC, direction);
    }

    @Test
    public void testGetSortDirectionInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Utils.getSortDirection("invalid"));
    }

    @Test
    public void testGetSortDirectionNull() {
        Sort.Direction direction = Utils.getSortDirection(null);
        assertEquals(Sort.Direction.ASC, direction);
    }

    @Test
    public void testGenerateEntityPageWithoutName() {
        BaseRepository<Object> mockRepository = mock(BaseRepository.class);
        Pageable pagingSort = PageRequest.of(0, 10);
        List<Object> testData = new ArrayList<>();
        Page<Object> mockPage = new PageImpl<>(testData);

        when(mockRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Object> resultPage = Utils.generateEntityPage(null, pagingSort, mockRepository);

        assertEquals(mockPage, resultPage);
    }

    @Test
    public void testGenerateEntityPageWithName() {
        BaseRepository<Object> mockRepository = mock(BaseRepository.class);
        Pageable pagingSort = PageRequest.of(0, 10);
        List<Object> testData = new ArrayList<>();
        Page<Object> mockPage = new PageImpl<>(testData);

        when(mockRepository.findByNameContaining(anyString(), any(Pageable.class))).thenReturn(mockPage);

        Page<Object> resultPage = Utils.generateEntityPage("testName", pagingSort, mockRepository);

        assertEquals(mockPage, resultPage);
    }

    @Test
    public void testParseResponseDataWithInterface() {
        List<Application> entities = new ArrayList<>();
        entities.add(new Application());

        Map<String, Object> response = new HashMap<>();

        Utils.parseResponseData(entities, DTO.class, response);

        assertEquals(entities, response.get("data"));
    }

    @Test
    public void testParseResponseDataWithConcreteClass() {
        Client client = new Client();

        Project project = new Project();
        project.setIdClient(client);

        ProjectDTO dto = new ProjectDTO();
        dto.setIdClient(project.getIdClient().getId());

        List<Project> entities = new ArrayList<>();
        entities.add(project);

        List<ProjectDTO> expectedResponse = new ArrayList<>();
        expectedResponse.add(dto);

        Map<String, Object> response = new HashMap<>();

        Utils.parseResponseData(entities, ProjectDTO.class, response);

        assertEquals(expectedResponse, response.get("data"));
    }

    @Test
    public void testConvertToDTODataException() {
        Object entity = new Object();
        assertThrows(RuntimeException.class, () -> Utils.convertToDTO(entity, null));
    }

    @Test
    public void testFindMatchingField() {
        Class<?> clazz = BaseModel.class;
        String fieldName = "nonExistentField";

        Field result = Utils.findMatchingField(clazz, fieldName);

        assertNull(result, "Expected the field to be null");
    }

    @Test
    public void testDeleteUserComposedEntities() {
        UserComposedRepository<UserApplication> mockRepository = Mockito.mock(UserComposedRepository.class);

        User userToDelete = new User();
        Application app = new Application();

        UserApplication userComposedEntity1 = new UserApplication();
        userComposedEntity1.setId(1);
        userComposedEntity1.setIdUser(userToDelete);
        userComposedEntity1.setIdApplication(app);
        UserApplication userComposedEntity2 = new UserApplication();
        userComposedEntity2.setId(2);
        userComposedEntity2.setIdUser(userToDelete);
        userComposedEntity2.setIdApplication(app);

        List<UserApplication> userComposedList = new ArrayList<>();
        userComposedList.add(userComposedEntity1);
        userComposedList.add(userComposedEntity2);

        when(mockRepository.findAllByIdUser(userToDelete)).thenReturn(userComposedList);

        Utils.deleteUserComposedEntities(mockRepository, userToDelete);

        for (UserComposedModel userEntity : userComposedList) {
            Mockito.verify(mockRepository).deleteById(userEntity.getId());
        }
    }

    @Test
    public void testVerifyExistence() {
        BaseRepository<Object> mockRepository = mock(BaseRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.of(Object.class));
        Optional<Object> response = Utils.verifyExistence(1, mockRepository, Object.class);
        assertInstanceOf(Class.class, response.get());
    }

    @Test
    public void testVerifyExistenceException() {
        BaseRepository<Object> mockRepository = mock(BaseRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(BaseNotFoundException.class, () -> Utils.verifyExistence(1, mockRepository, Object.class));
    }

}


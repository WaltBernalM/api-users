package com.bwl.apiusers.config;

import com.bwl.apiusers.dtos.DTO;
import com.bwl.apiusers.models.*;
import com.bwl.apiusers.repositories.*;
import com.bwl.apiusers.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.text.SimpleDateFormat;
import java.util.*;

public interface Loader {
    default <T extends BaseModel, ID> void jsonBaseModelParser(
            JpaRepository<T, ID> repository,
            TypeReference<List<T>> targTypeReference,
            List<T> dataToSave,
            String fileResource) {
        Logger log = LoggerFactory.getLogger(LoadDatabase.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            objectMapper.setDateFormat(dateFormat);

            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            Resource resource = new ClassPathResource(fileResource);
            dataToSave = objectMapper.readValue(
                    resource.getInputStream(),
                    targTypeReference
            );

//            repository.saveAll(dataToSave);
            // Parse the map of the dataInRepo
            Map<String, T> dataInRepo = new HashMap<>();
            repository.findAll().forEach(item -> {
                if(item instanceof User) {
                    dataInRepo.put(item.getUsername(), item);
                } else if (item instanceof Client) {
                    dataInRepo.put(item.getShortName(), item);
                } else {
                    dataInRepo.put(item.getKeycode(), item);
                }
            });

            List<T> nonDuplicates = new ArrayList<>();
            dataToSave.forEach(itemToSave -> {
                if(itemToSave instanceof User && !dataInRepo.containsKey(itemToSave.getUsername())) {
                    nonDuplicates.add(itemToSave);
                } else if (itemToSave instanceof Client && !dataInRepo.containsKey(itemToSave.getShortName())) {
                    nonDuplicates.add(itemToSave);
                } else if (
                        itemToSave instanceof Permission ||
                        itemToSave instanceof Project ||
                        itemToSave instanceof Application ||
                        itemToSave instanceof Profile){
                    if(!dataInRepo.containsKey(itemToSave.getKeycode())){
                        nonDuplicates.add(itemToSave);
                    }
                }
            });
            repository.saveAll(nonDuplicates);
        } catch (Exception e) {
            log.error("Error in loader ", e);
        }
    }

    CommandLineRunner initDatabase(
            ClientRepository clientRepository,
            ProjectRepository projectRepository,
            ApplicationRepository applicationRepository,
            ApplicationProjectRepository applicationProjectRepository,
            ProfileRepository profileRepository,
            PermissionRepository permissionRepository,
            ProfilePermissionRepository profilePermissionRepository,
            UserRepository userRepository,
            UserApplicationRepository userApplicationRepository,
            UserProfileRepository userProfileRepository
    );
}

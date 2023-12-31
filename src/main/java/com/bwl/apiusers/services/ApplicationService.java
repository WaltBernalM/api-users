package com.bwl.apiusers.services;

import com.bwl.apiusers.assemblers.ApplicationModelAssembler;
import com.bwl.apiusers.dtos.ApplicationDTO;
import com.bwl.apiusers.models.Application;
import com.bwl.apiusers.repositories.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService extends GenericReadService <Application, ApplicationDTO, ApplicationRepository, ApplicationModelAssembler> {
    public ApplicationService(ApplicationRepository repository, ApplicationModelAssembler assembler) {
        super(repository, assembler, Application.class, ApplicationDTO.class);
    }
}

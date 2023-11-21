package com.bwl.apiusers.assemblers;

import com.bwl.apiusers.dtos.ProjectDTO;
import com.bwl.apiusers.models.Project;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ProjectModelAssembler extends DTOModelAssembler<ProjectDTO, Project> {
}

package com.bwl.apiusers.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UpdateClientDTO implements DTO {
    private String name;
    private String shortName;
    private Boolean enabled;
}

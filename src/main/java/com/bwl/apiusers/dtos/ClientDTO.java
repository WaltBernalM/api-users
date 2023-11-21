package com.bwl.apiusers.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ClientDTO implements DTO {
    private Integer id;
    private String name;
    private String shortName;
    private Boolean enabled;
}

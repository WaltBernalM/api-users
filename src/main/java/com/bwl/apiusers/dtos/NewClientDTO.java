package com.bwl.apiusers.dtos;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class NewClientDTO implements DTO {
    private String name;
    private String shortName;
}

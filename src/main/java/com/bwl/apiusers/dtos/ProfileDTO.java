package com.bwl.apiusers.dtos;

import com.bwl.apiusers.dtos.DTO;
import lombok.Data;

@Data
public class ProfileDTO implements DTO {
    private Integer id;
    private String name;
    private String description;
    private String keycode;
}

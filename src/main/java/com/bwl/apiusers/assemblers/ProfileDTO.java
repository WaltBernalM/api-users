package com.bwl.apiusers.assemblers;

import lombok.Data;

@Data
public class ProfileDTO {
    private Integer id;
    private String name;
    private String description;
    private String keycode;
}

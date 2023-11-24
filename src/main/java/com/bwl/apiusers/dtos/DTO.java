package com.bwl.apiusers.dtos;

public interface DTO {
    default String getKeycode() {return null;}
    default String getShortName() {return null;}
    default String getUsername() {return null;}
}

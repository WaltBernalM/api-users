package com.bwl.apiusers.models;

public interface BaseModel {
    default String getKeycode() {return null;}
    default String getShortName() {return null;}
    default String getUsername() {return null;}
}

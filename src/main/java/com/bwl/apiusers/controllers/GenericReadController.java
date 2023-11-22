package com.bwl.apiusers.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface GenericReadController {

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Integer id);

    @GetMapping("")
    public ResponseEntity<?> all(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + Integer.MAX_VALUE) int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    );
}


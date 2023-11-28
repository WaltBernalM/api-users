package com.bwl.apiusers.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public interface GenericCRUDController<N, U> extends GenericReadController {

    @PostMapping("")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<?> postNew(@RequestBody N newEntityDTO);

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOne(@PathVariable Integer id, @RequestBody U updateEntityDTO);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<?> deleteOne(@PathVariable Integer id);
}

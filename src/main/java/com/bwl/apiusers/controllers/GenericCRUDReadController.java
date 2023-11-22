package com.bwl.apiusers.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface GenericCRUDReadController<N, U> extends GenericReadController {

    @PostMapping("/signup")
    public ResponseEntity<?> postNew(@RequestBody N newEntityDTO);

    @PatchMapping("/{id}/update")
    public ResponseEntity<?> updateOne(@PathVariable Integer id, @RequestBody U updateEntityDTO);

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Integer id);
}

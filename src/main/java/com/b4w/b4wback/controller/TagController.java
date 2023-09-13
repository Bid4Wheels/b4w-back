package com.b4w.b4wback.controller;

import com.b4w.b4wback.service.interfaces.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTags(){
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAllTags());
    }
}

package com.jaewon.ToDoProject.controller;

import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.repository.PageRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {
    @Autowired
    private PageRepository pageRepository;

    @GetMapping("/test")
    public ResponseEntity<Page> test(Model model){
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}

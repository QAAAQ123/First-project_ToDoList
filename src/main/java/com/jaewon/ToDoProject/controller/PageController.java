package com.jaewon.ToDoProject.controller;

import com.jaewon.ToDoProject.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @Autowired
    private PageRepository pageRepository;

    @GetMapping
    public String showPages(){
        return "";
    }


}

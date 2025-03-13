package com.jaewon.ToDoProject.restController;

import com.jaewon.ToDoProject.dto.PageDto;
import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.repository.PageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class PageRestController {
    @Autowired
    private PageRepository pageRepository;

    @GetMapping("/pages")
    public ResponseEntity<List<PageDto>> showPages(Model model){
        List<Page> pageList = pageRepository.findAll();
        log.info(pageList.toString());
        List<PageDto> pageDtoList = pageList.stream()
                .map(Page -> Page.toDto())
                .collect(Collectors.toList());
        log.info(pageDtoList.toString());
        return ResponseEntity.status(HttpStatus.OK).body(pageDtoList);
    }
}

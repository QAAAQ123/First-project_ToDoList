package com.jaewon.ToDoProject.restController;

import com.jaewon.ToDoProject.dto.PageDto;
import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.repository.PageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class PageRestController {
    @Autowired
    private PageRepository pageRepository;

    //메인 화면(페이지 전체 보기)
    //예외처리 없음
    @GetMapping("/pages")
    public ResponseEntity<List<PageDto>> showPages(Model model){
        List<Page> pageList = pageRepository.findAll();
        //log.info("Entities: {}",pageList.toString());
        List<PageDto> pageDtoList = pageList.stream()
                .map(Page -> Page.toDto())
                .collect(Collectors.toList());
        log.info("Get DTOs: {}", pageDtoList.toString());
        return ResponseEntity.status(HttpStatus.OK).body(pageDtoList);
    }

    //단일 페이지 즉,할일 목록이 보여지는 페이지 보이기
    //예외처리 id가 없을때
    @GetMapping("/pages/{id}")
    public ResponseEntity<PageDto> showPage(@PathVariable Long id,Model model){
        Page page = pageRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 데이터가 없습니다."));;
        PageDto pageDto = page.toDto();
        log.info("Get DTO: {}", pageDto.toString());
        return ResponseEntity.status(HttpStatus.OK).body(pageDto);
    }

    //페이지 새로 생성
    //예외처리 id가 중복될때나 title이 null일때
    @PostMapping("/pages")
    public ResponseEntity<PageDto> createPage(@RequestBody PageDto pageDto){
        Page page = pageDto.toEntity();
        Page createTarget = pageRepository.save(page);
        PageDto createTargetDto = createTarget.toDto();
        return ResponseEntity.status(HttpStatus.OK).body(createTargetDto);
    }

    //페이지 제목 수정
    //예외처리 id가 변경 되었거나 title이 null(수정시 "" 로 작성한 경우)
    @Transactional
    @PatchMapping("/pages/{id}")
    public ResponseEntity<PageDto> updatePage(@RequestBody PageDto pageDto,@PathVariable Long id){
        Page page = pageDto.toEntity(); //수정하려고 들어온 title데이터
        Page updateTarget = pageRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 데이터가 없습니다."));;
        updateTarget.mergeWithExistingData(page);
        Page updated = pageRepository.save(updateTarget);
        PageDto updatedDto = updated.toDto();
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    //페이지 삭제
    //예외 처리 삭제 하려는 id가 없을
    @Transactional
    @DeleteMapping("/pages/{id}")
    public ResponseEntity<PageDto> deletePage(@PathVariable Long id){
        Page deleteTarget = pageRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 데이터가 없습니다."));
        pageRepository.delete(deleteTarget);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

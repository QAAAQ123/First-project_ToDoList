package com.jaewon.ToDoProject.restController;

import com.jaewon.ToDoProject.dto.PageDto;
import com.jaewon.ToDoProject.dto.ToDoDto;
import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.entity.ToDo;
import com.jaewon.ToDoProject.repository.PageRepository;
import com.jaewon.ToDoProject.repository.ToDoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/*
1.전체 페이지 get 요청 showPages-성공
2.페이지 생성 post 요청 createPage-성공
3.페이지 수정 patch 요청 updatePage-실패
4.페이지 삭제 delete 요청 deletePage-실패
5.단일 페이지와 페이지 id에 해당하는 할일 목록 get 요청 showPage-실패
6.단일 페이지의 할일 목록을 생성 post 요청 createToDo-실패
7.단일 페이지의 할일 목록을 수정하는 patch 요청 updateToDo-실패
8.단일 페이지의 할일 목록을 제거하는 delete 요청 deleteToDo-실패
*/
@org.springframework.web.bind.annotation.RestController
@Slf4j
public class RestController {
    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    //메인 화면(페이지 전체 보기)
    //예외처리 없음
    @GetMapping("/pages")
    public ResponseEntity<List<PageDto>> showPages(){
        List<Page> pageList = pageRepository.findAll();
        //log.info("Entities: {}",pageList.toString());
        List<PageDto> pageDtoList = pageList.stream()
                .map(Page -> Page.toDto())
                .collect(Collectors.toList());
        log.info("showPages DTOs: {}", pageDtoList.toString());
        return ResponseEntity.status(HttpStatus.OK).body(pageDtoList);
    }

    //페이지 새로 생성
    //예외처리 id가 중복될때나 title이 null일때
    @Transactional
    @PostMapping("/pages")
    public ResponseEntity<PageDto> createPage(@RequestBody PageDto pageDto){
        Page page = pageDto.toEntity();
        Page createTarget = pageRepository.save(page);
        PageDto createTargetDto = createTarget.toDto();
        log.info("createPage DTO: {}", createTargetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createTargetDto);
    }

    //페이지 제목 수정
    //예외처리 id가 변경 되었거나 title이 null(수정시 "" 로 작성한 경우)
    @Transactional
    @PatchMapping("/pages/{pageId}")
    public ResponseEntity<PageDto> updatePage(@RequestBody PageDto pageDto,@PathVariable Long pageId){
        Page page = pageDto.toEntity(); //수정하려고 들어온 title데이터
        Page updateTarget = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));;
        updateTarget.mergeWithExistingData(page);
        Page updated = pageRepository.save(updateTarget);
        PageDto updatedDto = updated.toDto();
        log.info("updatePage DTO: {}",updatedDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    //페이지 삭제
    //예외 처리 삭제 하려는 id가 없을
    @Transactional
    @DeleteMapping("/pages/{pageId}")
    public ResponseEntity<PageDto> deletePage(@PathVariable Long pageId){
        Page deleteTarget = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        pageRepository.delete(deleteTarget);
        log.info("deletePage DTO:{}",deleteTarget.toString());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //단일 페이지 즉,할일 목록이 보여지는 페이지 보이기
    //예외처리 id가 없을때
    @GetMapping("/pages/{pageId}")
    public ResponseEntity<PageDto> showPage(@PathVariable Long pageId){
        //page 처리
        Page page = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        PageDto pageDto = page.toDto();
        log.info("showPage page DTO: {}", pageDto.toString());

        //toDo 처리
        List<ToDo> toDoList = toDoRepository.findAll();
        List<ToDoDto> toDoDtoList = toDoList.stream()
                .map(toDo -> (ToDoDto) toDo.toDto())
                .collect(Collectors.toList());
        log.info("showPage todo DTOs: {}",toDoDtoList.toString());

        //todo,page 합치기
        pageDto.setToDoList(toDoDtoList);

        return ResponseEntity.status(HttpStatus.OK).body(pageDto);
    }

    //할일 생성
    //예외 처리 할일의 내용이 없거나 id에 오류가 있을 경우
    @Transactional
    @PostMapping("/pages/{pageId}")
    public ResponseEntity<PageDto> createToDo(@PathVariable Long pageId, @RequestBody ToDoDto toDoDto){
        //page 처리
        Page page = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        PageDto pageDto = page.toDto();
        log.info("createToDo page DTO: {}", pageDto.toString());

        //todo처리
        ToDo updateTarget = toDoDto.toEntity(page);
        ToDo saved = toDoRepository.save(updateTarget);
        ToDoDto savedDto = saved.toDto();
        log.info("createToDo todo DTO: {}",savedDto.toString());

        //page todo합치기
        List<ToDo> toDoList = toDoRepository.findByPageId(page);
        List<ToDoDto> toDoDtoList = toDoList.stream()
                .map(ToDo::toDto)
                .collect(Collectors.toList());
        pageDto.setToDoList(toDoDtoList);

        return ResponseEntity.status(HttpStatus.CREATED).body(pageDto);
    }

    //할일 수정
    @Transactional
    @PatchMapping("/pages/{pageId}/{toDoId}")
    public ResponseEntity<PageDto> updateToDo(@PathVariable Long pageId,@PathVariable Long toDoId, @RequestBody ToDoDto toDoDto){
        //page
        Page page = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        PageDto pageDto = page.toDto();
        log.info("updateToDo page DTO: {}",pageDto);

        //todo
        ToDo updateTarget = toDoRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 할일을 생성 할 수 없습니다."));
        updateTarget.mergeWithExistingData(toDoDto);
        ToDo updatedTarget = toDoRepository.save(updateTarget);
        ToDoDto updatedTargetDto = updatedTarget.toDto();
        log.info("updateToDo todo DTO: {}",updatedTargetDto.toString());

        //page todo합치기
        List<ToDo> toDoList = toDoRepository.findByPageId(page);
        List<ToDoDto> toDoDtoList = toDoList.stream()
                .map(ToDo::toDto)
                .collect(Collectors.toList());
        pageDto.setToDoList(toDoDtoList);

        return ResponseEntity.status(HttpStatus.OK).body(pageDto);
    }

    @Transactional
    @DeleteMapping("/pages/{pageId}/{toDoId}")
    public ResponseEntity<PageDto> deleteToDo(@PathVariable Long pageId,@PathVariable Long toDoId){
        //page
        Page page = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        PageDto pageDto = page.toDto();
        log.info("deleteToDo page Dto: {}",pageDto.toString());

        //todo
        ToDo deleteTarget = toDoRepository.findById(toDoId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 할일을 삭제 할 수 없습니다."));
        toDoRepository.delete(deleteTarget);
        log.info("deleteToDo todo DTO: {}",deleteTarget.toString());

        //page todo합치기
        List<ToDo> toDoList = toDoRepository.findByPageId(page);
        List<ToDoDto> toDoDtoList = toDoList.stream()
                .map(ToDo::toDto)
                .collect(Collectors.toList());
        pageDto.setToDoList(toDoDtoList);

        return ResponseEntity.status(HttpStatus.OK).body(pageDto);
    }
}

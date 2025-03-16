package com.jaewon.ToDoProject.restController;

import com.jaewon.ToDoProject.dto.PageDto;
import com.jaewon.ToDoProject.dto.ToDoDto;
import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.entity.ToDo;
import com.jaewon.ToDoProject.repository.PageRepository;
import com.jaewon.ToDoProject.repository.ToDoRepository;
import com.jaewon.ToDoProject.service.Service;
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
순서
1. 초기 설정
2. entity,dto,repository,controller,service만들기
3. controller에서 page 기능 부터 구현
4. controller에서 page와 todo 기능 구현
5. service로 기능 이전
6. 예외 처리
7. 코드 리팩터링
8. 메인 페이지 프론트 엔드 react로 구현
9. 단일 페이지 프론트 엔드 react로 구현
10. 최종 확인
11. 배포

메소드
1.전체 페이지 get 요청 showPages
2.페이지 생성 post 요청 createPage
3.페이지 수정 patch 요청 updatePage
4.페이지 삭제 delete 요청 deletePage
5.단일 페이지와 페이지 id에 해당하는 할일 목록 get 요청 showPage
6.단일 페이지의 할일 목록을 생성 post 요청 createToDo
7.단일 페이지의 할일 목록을 수정하는 patch 요청 updateToDo
8.단일 페이지의 할일 목록을 제거하는 delete 요청 deleteToDo

페이지 데이터: id,title
할일 데이터: id,pageId,content
*/
@org.springframework.web.bind.annotation.RestController
@RequestMapping("/pages")
public class RestController {
   @Autowired
   private Service service;

    //메인 화면(페이지 전체 보기)
    @GetMapping("")
    public ResponseEntity<List<PageDto>> showPages(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.showPagesService());
    }

    //페이지 새로 생성
    @Transactional
    @PostMapping("")
    public ResponseEntity<PageDto> createPage(@RequestBody PageDto pageDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createPageService(pageDto));
    }

    //페이지 제목 수정
    @Transactional
    @PatchMapping("/{pageId}")
    public ResponseEntity<PageDto> updatePage(@RequestBody PageDto pageDto,@PathVariable(name = "pageId") Long pageId){
        PageDto updatedDto = service.updatePageService(pageDto,pageId);
        return (updatedDto == null) ?
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build():
                ResponseEntity.status(HttpStatus.OK).body(service.updatePageService(pageDto,pageId));
    }

    //페이지 삭제
    @Transactional
    @DeleteMapping("/{pageId}")
    public ResponseEntity deletePage(@PathVariable(name = "pageId") Long pageId){
        service.deletePageService(pageId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //단일 페이지 즉,할일 목록이 보여지는 페이지 보이기
    @GetMapping("/{pageId}")
    public ResponseEntity<PageDto> showPage(@PathVariable(name = "pageId") Long pageId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.showPageService(pageId));
    }

    //할일 생성
    @Transactional
    @PostMapping("/{pageId}")
    public ResponseEntity<PageDto> createToDo(@PathVariable(name = "pageId") Long pageId, @RequestBody ToDoDto toDoDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createToDoService(pageId,toDoDto));
    }

    //할일 수정
    @Transactional
    @PatchMapping("/{pageId}/{toDoId}")
    public ResponseEntity<PageDto> updateToDo(@PathVariable(name = "pageId") Long pageId,
                                              @PathVariable(name = "toDoId") Long toDoId, @RequestBody ToDoDto toDoDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.updateToDoService(pageId,toDoId,toDoDto));
    }

    //할일 삭제
    @Transactional
    @DeleteMapping("/{pageId}/{toDoId}")
    public ResponseEntity<PageDto> deleteToDo(@PathVariable(name = "pageId") Long pageId,@PathVariable(name = "toDoId") Long toDoId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.deleteToDoService(pageId,toDoId));
    }
}

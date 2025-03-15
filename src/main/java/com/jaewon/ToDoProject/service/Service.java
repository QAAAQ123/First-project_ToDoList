package com.jaewon.ToDoProject.service;

import com.jaewon.ToDoProject.dto.PageDto;
import com.jaewon.ToDoProject.dto.ToDoDto;
import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.entity.ToDo;
import com.jaewon.ToDoProject.repository.PageRepository;
import com.jaewon.ToDoProject.repository.ToDoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Slf4j
public class Service {
    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    //메인 화면(페이지 전체 보기)
    //예외처리 없음
    public List<PageDto> showPagesService(){
        List<Page> pageList = pageRepository.findAll();
        //log.info("Entities: {}",pageList.toString());
        List<PageDto> pageDtoList = pageList.stream()
                .map(Page -> Page.toDto())
                .collect(Collectors.toList());
        log.info("showPages DTOs: {}", pageDtoList.toString());
        return pageDtoList;
    }

    //페이지 새로 생성
    //예외처리 id가 중복될때나 title이 null일때
    public PageDto createPageService(PageDto pageDto){
        Page page = pageDto.toEntity();
        Page createTarget = pageRepository.save(page);
        PageDto createTargetDto = createTarget.toDto();
        log.info("createPage DTO: {}", createTargetDto);
        return createTargetDto;
    }

    //페이지 제목 수정
    //예외처리 id가 변경 되었거나 title이 null(수정시 "" 로 작성한 경우)
    public PageDto updatePageService(PageDto pageDto,Long pageId){
        Page page = pageDto.toEntity(); //수정하려고 들어온 title데이터
        log.info("pageDto:{},pageEntity:{},pageId:{}",pageDto,page,pageId);
        if(!page.getId().equals(pageId)) {
            log.info("URL에서 요청한 페이지와 사용자가 요청한 페이지가 다릅니다.");
            return null;
        }
        Page updateTarget = pageRepository.findById(pageId).
                orElseThrow(() -> new IllegalArgumentException("해당 id의 페이지가 없습니다."));;
        updateTarget.mergeWithExistingData(page);
        Page updated = pageRepository.save(updateTarget);
        PageDto updatedDto = updated.toDto();
        log.info("updatePage DTO: {}",updatedDto);
        return updatedDto;
    }

    //페이지 삭제
    //예외 처리 삭제 하려는 id가 없을때
    public void deletePageService(Long pageId){
        //todo 삭제
        List<ToDo> deleteTargetToDoList = toDoRepository.findByPageId(pageRepository.findById(pageId)
                . orElseThrow(() -> new IllegalArgumentException("해당 id의 페이지가 없습니다.")));
        toDoRepository.deleteAll(deleteTargetToDoList);
        log.info("deleteToDo entity:{}",deleteTargetToDoList);

        //page 삭제
        Page deleteTarget = pageRepository.findById(pageId).
                orElseThrow(() -> new IllegalArgumentException("해당 id의 페이지가 없습니다."));
        pageRepository.delete(deleteTarget);
        log.info("deletePage entity:{}",deleteTarget.toString());
    }

    //단일 페이지 즉,할일 목록이 보여지는 페이지 보이기
    //예외처리 id가 없을때
    public PageDto showPageService(Long pageId){
        //page 처리
        Page page = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        PageDto pageDto = page.toDto();
        log.info("showPage page DTO: {}", pageDto.toString());

        //toDo 처리
        List<ToDo> toDoList = toDoRepository.findByPageId(pageRepository.findById(pageId)
                . orElseThrow(() -> new IllegalArgumentException("해당 id의 페이지가 없습니다.")));
        List<ToDoDto> toDoDtoList = toDoList.stream()
                .map(toDo -> (ToDoDto) toDo.toDto())
                .collect(Collectors.toList());
        log.info("showPage todo DTOs: {}",toDoDtoList.toString());

        //todo,page 합치기
        pageDto.setToDoList(toDoDtoList);

        return pageDto;
    }

    //할일 생성
    //예외 처리 할일의 내용이 없거나 id에 오류가 있을 경우
    public PageDto createToDoService(Long pageId,ToDoDto toDoDto){
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

        return pageDto;
    }

    // 할일 수정
    public PageDto updateToDoService(Long pageId,Long toDoId,ToDoDto toDoDto){
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

        return pageDto;
    }

    //할일 삭제
    public PageDto deleteToDoService(Long pageId,Long toDoId){
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

        return pageDto;
    }
}

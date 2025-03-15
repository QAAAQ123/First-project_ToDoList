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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

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
    //예외처리
    public List<PageDto> showPagesService(){
        List<Page> pageList = pageRepository.findAll();
        List<PageDto> pageDtoList = pageList.stream()
                .map(Page -> Page.toDto())
                .collect(Collectors.toList());
        log.info("showPages DTOs: {}", pageDtoList.toString());
        return pageDtoList;
    }

    //페이지 새로 생성
    //예외처리 1. 페이지 id가 이미 존재할 경우,2.페이지가 생성되지 않을 경우
    public PageDto createPageService(PageDto pageDto){
        if(pageDto.getId() != null && pageRepository.existsById(pageDto.getId())){
            log.error("이미 존재하는 pageId: {}",pageDto.getId());
            throw new DataIntegrityViolationException("해당 ID의 페이지가 이미 존재합니다.");
        }
        Page page = pageDto.toEntity();
        Page createTarget = pageRepository.save(page);
        if(createTarget == null  || createTarget.getId() == null){
            log.error("페이지 생성 실패: {}",pageDto);
            throw new IllegalStateException("페이지 생성에 실패 했습니다.");
        }
        PageDto createTargetDto = createTarget.toDto();

        return createTargetDto;
    }

    //페이지 제목 수정
    //예외처리 1 dto와 url에서 받은 id가 다를 경우
    public PageDto updatePageService(PageDto pageDto,Long pageId){
        Page page = pageDto.toEntity(); //수정하려고 들어온 title데이터
        log.info("pageDto:{},pageEntity:{},pageId:{}",pageDto,page,pageId);
        if(!page.getId().equals(pageId)) {
            log.error("URL에서 요청한 페이지와 사용자가 요청한 페이지id가 다릅니다.");
            throw new IllegalArgumentException("URL에서 요청한 페이지와 사용자가 요청한 페이지id가 다릅니다.");
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
    //예외 처리1.삭제하려는 페이지의 id가 없을때,3. 데이터가 삭제되지 않았을때
    public void deletePageService(Long pageId){
        //todo 삭제
        List<ToDo> deleteTargetToDoList = toDoRepository.findByPageId(pageRepository.findById(pageId)
                . orElseThrow(() -> new IllegalArgumentException("해당 id의 페이지가 없습니다.")));
        toDoRepository.deleteAll(deleteTargetToDoList);
        if(deleteTargetToDoList != null){
            log.error("페이지 삭제 과정에서 할일목록이 제거되지 않았습니다.");
            throw new EmptyResultDataAccessException("해당 페이지의 ToDo가 제거되지 않았습니다.",0);
        }
        log.info("deleteToDo entity:{}",deleteTargetToDoList);

        //page 삭제
        Page deleteTarget = pageRepository.findById(pageId).
                orElseThrow(() -> new IllegalArgumentException("해당 id의 페이지가 없습니다."));
        pageRepository.delete(deleteTarget);
        if(deleteTarget != null){
            log.error("페이지 삭제 과정에서 페이지가 제거되지 않았습니다.");
            throw new EmptyResultDataAccessException("해당 페이지가 제거되지 않았습니다.",0);
        }
        log.info("deletePage entity:{}",deleteTarget.toString());
    }

    //단일 페이지 즉,할일 목록이 보여지는 페이지 
    //예외처리
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
    //예외 처리  todo의 id가 이미 존재하는 경우
    public PageDto createToDoService(Long pageId,ToDoDto toDoDto){
        //page 처리
        Page page = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        PageDto pageDto = page.toDto();
        log.info("createToDo page DTO: {}", pageDto.toString());

        //todo처리
        if(toDoDto.getId() != null &&  toDoRepository.existsById(toDoDto.getId())){
            log.error("생성하려는 Todo의 id가 이미 존재합니다.");
            throw new DataIntegrityViolationException("Todo의 id가 이미 존재합니다.");
        }
        ToDo createTarget = toDoDto.toEntity(page);
        ToDo saved = toDoRepository.save(createTarget);
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
    //예외: url요청과 클라이언트 요청의 page,todo id가 다른 경우
    public PageDto updateToDoService(Long pageId,Long toDoId,ToDoDto toDoDto){
        if(pageId != toDoDto.getPageId() || toDoId != toDoDto.getId() ){
            log.error("URL과 클라이언트 요청의 page나 toDo의 id가 서로 다릅니다.");
            throw new IllegalArgumentException("URL과 클라이언트 사이의 page or todo id가 서로 다릅니다.");
        }

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
    //예외 삭제하려는 페이지id나 todoid가 없는 경우
    public PageDto deleteToDoService(Long pageId,Long toDoId){
        //page
        Page page = pageRepository.findById(pageId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 페이지가 없습니다."));
        PageDto pageDto = page.toDto();
        log.info("deleteToDo page Dto: {}",pageDto.toString());

        //todo
        ToDo deleteTargetToDoList = toDoRepository.findById(toDoId).
                orElseThrow(() -> new EntityNotFoundException("해당 id의 할일을 삭제 할 수 없습니다."));
        toDoRepository.delete(deleteTargetToDoList);
        if(deleteTargetToDoList != null){
            log.error("할일목록이 제거되지 않았습니다.");
            throw new EmptyResultDataAccessException("ToDo가 제거되지 않았습니다.",0);
        }
        log.info("deleteToDo todo DTO: {}", deleteTargetToDoList.toString());

        //page todo합치기
        List<ToDo> toDoList = toDoRepository.findByPageId(page);
        List<ToDoDto> toDoDtoList = toDoList.stream()
                .map(ToDo::toDto)
                .collect(Collectors.toList());
        pageDto.setToDoList(toDoDtoList);

        return pageDto;
    }
}

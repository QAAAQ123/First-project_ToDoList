package com.jaewon.ToDoProject.dto;

import com.jaewon.ToDoProject.entity.Page;
import lombok.*;

import java.util.List;
import java.util.SplittableRandom;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PageDto {
    private Long id;
    private String title;

    private List<ToDoDto> toDoList;

    public PageDto(Long id,String title){
        this.id = id;
        this.title = title;
    }

    public Page toEntity() {
        return new Page(id,title);
    }
}

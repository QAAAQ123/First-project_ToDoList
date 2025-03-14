package com.jaewon.ToDoProject.dto;

import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.entity.ToDo;
import com.jaewon.ToDoProject.repository.PageRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ToDoDto {
    private Long id;
    private Long pageId;
    private String content;

    public ToDo toEntity(Page page) {
        return new ToDo(id,page,content);
    }
}


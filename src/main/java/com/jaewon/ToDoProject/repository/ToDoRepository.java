package com.jaewon.ToDoProject.repository;

import com.jaewon.ToDoProject.dto.ToDoDto;
import com.jaewon.ToDoProject.entity.Page;
import com.jaewon.ToDoProject.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo,Long> {
    List<ToDo> findByPageId(Page page);


}

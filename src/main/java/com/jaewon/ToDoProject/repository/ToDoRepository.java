package com.jaewon.ToDoProject.repository;

import com.jaewon.ToDoProject.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo,Long> {
}

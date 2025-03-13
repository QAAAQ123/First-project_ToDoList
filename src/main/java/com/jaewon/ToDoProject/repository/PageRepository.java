package com.jaewon.ToDoProject.repository;

import com.jaewon.ToDoProject.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageRepository extends JpaRepository<Page,Long> {
}

package com.jaewon.ToDoProject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    //페이지 id 자동 생성
    @Id
    @Column(name = "page_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //페이지 제목
    @Column
    private String title;
}

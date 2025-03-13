package com.jaewon.ToDoProject.entity;

import com.jaewon.ToDoProject.dto.PageDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "page")
public class Page {
    //페이지 id 자동 생성
    @Id
    @Column(name = "page_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //페이지 제목
    @Column
    private String title;

    public PageDto toDto() {
        return new PageDto(id,title);
    }

    public void mergeWithExistingData(Page inputPage) {
        if(inputPage.title != null)
            this.title = inputPage.title;
    }
}

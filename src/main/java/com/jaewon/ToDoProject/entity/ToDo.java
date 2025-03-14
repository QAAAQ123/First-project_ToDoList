package com.jaewon.ToDoProject.entity;

import com.jaewon.ToDoProject.dto.ToDoDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todo")
public class ToDo {
    @Id
    @Column(name = "list_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page pageId;

    @Column
    private String content;

    public ToDoDto toDto() {
        return new ToDoDto(id,pageId.getId(),content);
    }

    public void mergeWithExistingData(ToDoDto toDoDto) {
        if(toDoDto.getContent() != null)
            this.content = toDoDto.getContent();
    }
}

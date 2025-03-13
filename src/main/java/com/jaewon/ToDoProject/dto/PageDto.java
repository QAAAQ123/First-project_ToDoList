package com.jaewon.ToDoProject.dto;

import lombok.*;

import java.util.SplittableRandom;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PageDto {
    private Long id;
    private String title;
}

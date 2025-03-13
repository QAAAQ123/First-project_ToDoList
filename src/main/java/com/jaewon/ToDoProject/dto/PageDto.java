package com.jaewon.ToDoProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.SplittableRandom;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageDto {
    private Long id;
    private String title;
}

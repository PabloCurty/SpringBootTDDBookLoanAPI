package com.curty.libraryAPI.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data in addition to creating Getters and Setters also creates ToString and HashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}

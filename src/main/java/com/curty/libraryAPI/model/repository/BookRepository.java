package com.curty.libraryAPI.model.repository;

import com.curty.libraryAPI.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

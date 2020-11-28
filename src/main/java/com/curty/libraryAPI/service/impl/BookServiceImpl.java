package com.curty.libraryAPI.service.impl;

import com.curty.libraryAPI.exception.BusinessException;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.repository.BookRepository;
import com.curty.libraryAPI.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn already filled");
        }
        return repository.save(book);
    }
}

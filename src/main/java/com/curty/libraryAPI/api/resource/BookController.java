package com.curty.libraryAPI.api.resource;

import com.curty.libraryAPI.api.dto.BookDOT;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDOT create(@RequestBody BookDOT dto){
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDOT.class);
    }
}

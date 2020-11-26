package com.curty.libraryAPI.service;

import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.repository.BookRepository;
import com.curty.libraryAPI.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void SetUp(){
        service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Must save a book")
    public void saveBookTest(){
        //scenario
        Book book = Book.builder().isbn("123").title("As aventuras").author("Fulano").build();
        Mockito.when(repository.save(book))
                .thenReturn(Book.builder()
                                .id((long) 11)
                                .isbn("123")
                                .title("As aventuras")
                                .author("Fulano").build());
        //execution
        Book savedBook = service.save(book);

        //verification
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }
}

package com.curty.libraryAPI.service;

import com.curty.libraryAPI.exception.BusinessException;
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

//Add in test class all the functions from spring
@ExtendWith(SpringExtension.class)
//Rollback all the operations in test scope
@ActiveProfiles("test")
public class BookServiceTest {
    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void SetUp(){
        service = new BookServiceImpl(repository);
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").title("As aventuras").author("Fulano").build();
    }

    @Test
    @DisplayName("Must save a book")
    public void saveBookTest(){
        //scenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
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

    @Test
    @DisplayName("Must call business error when try to save book with duplicated Isbn")
    public void shouldNotSaveBookWithDuplicatedIsbn(){
        //scenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        //execution
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verifications
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn already filled");
        Mockito.verify(repository, Mockito.never()).save(book);
    }
}

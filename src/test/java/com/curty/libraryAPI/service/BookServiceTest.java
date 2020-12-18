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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Should get a book per Id")
    public void getByIdTest(){
        //scenario
        Long id = Long.valueOf(11);

        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execution
        Optional<Book> foundBook = service.getById(id);

        //verifications
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }


    @Test
    @DisplayName("Should get a empty book when Id doesn't exist in DB")
    public void bookNotFoundByIdTest(){
        //scenario
        Long id = Long.valueOf(11);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execution
        Optional<Book> foundBook = service.getById(id);

        //verifications
        Assertions.assertThat(foundBook.isPresent()).isFalse();
    }


    @Test
    @DisplayName("Should delete a book")
    public void deleteBookTest(){
        //scenario
        Long id = Long.valueOf(11);
        Book book = createValidBook();
        book.setId(id);

        //execution
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        //verifications
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Should return an error when try to delete an no exist book")
    public void deleteInvalidBookTest(){
        //scenario
        Book book = new Book();

        //execution
        org.junit.jupiter.api.Assertions
                .assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        //verifications
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Should return an error when try to update an no exist book")
    public void updateInvalidBookTest(){
        //scenario
        Book book = new Book();

        //execution
        org.junit.jupiter.api.Assertions
                .assertThrows(IllegalArgumentException.class, () -> service.update(book));

        //verifications
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Should update a book")
    public void updateBookTest(){
        //scenario
        Long id = Long.valueOf(11);
        Book updatingBook = Book.builder().id(id).build();
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        //execution
        Book book = service.update(updatingBook);

        //verification
        Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }


    @Test
    @DisplayName("Should filter properties of books ")
    public void findBookTest(){

        //scenario
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execution
        Page<Book> result = service.find(book, pageRequest);

        //verifications
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(list);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should get a book by Isbn")
    public void findBookByIsbnTest(){
        //scenario
        String isbn = "1230";
        Mockito.when(repository.findByIsbn(isbn))
                .thenReturn(Optional.of(Book.builder().id((long) 11).isbn(isbn).build()));
        //execution
        Optional<Book> book = service.getBookByIsbn(isbn);

        //verification
        Assertions.assertThat(book.isPresent()).isTrue();
        Assertions.assertThat(book.get().getId()).isEqualTo(11);
        Assertions.assertThat(book.get().getIsbn()).isEqualTo(isbn);
        Mockito.verify(repository, Mockito.times(1)).findByIsbn(isbn);



    }
}

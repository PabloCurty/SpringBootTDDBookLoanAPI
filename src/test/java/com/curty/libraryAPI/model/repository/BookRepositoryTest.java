package com.curty.libraryAPI.model.repository;

import com.curty.libraryAPI.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
//Create a DB instance in memory, when stop clean
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    private Book createNewBook() {
        return Book.builder().title("Aventuras").author("Fulano").isbn("123").build();
    }

    @Test
    @DisplayName("Should return true when exist a book in DB with the ispn informed")
    public void returnTrueWhenIsbnExist(){
        //scenario
        String isbn = "123";
        Object book = createNewBook();
        entityManager.persist(book);
        //execution
        boolean exists = repository.existsByIsbn(isbn);
        //verification
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when doesn't exist a book in DB with the ispn informed")
    public void returnFalseWhenIsbnDoesNotExist(){
        //scenario
        String isbn = "123";
        //execution
        boolean exists = repository.existsByIsbn(isbn);
        //verification
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get a book by Id")
    public void findByIdTest(){
        //scenario
        Book book = createNewBook();
        entityManager.persist(book);

        //execution
        Optional<Book> foundBook = repository.findById(book.getId());

        //verfications
        Assertions.assertThat(foundBook.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest(){
        //scenario
        Book book = createNewBook();

        //execution
        Book savedBook = repository.save(book);

        //verfications
        Assertions.assertThat(savedBook.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete a book")
    public void deleteBookTest(){
        //scenario
        Book book = createNewBook();
        entityManager.persist(book);
        Book foundBook = entityManager.find(Book.class, book.getId());
        //execution
        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());

        //verfications
        Assertions.assertThat(deletedBook).isNull();

    }
}

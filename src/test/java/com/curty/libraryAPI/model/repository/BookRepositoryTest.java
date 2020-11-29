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

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
//Create a DB instance in memory, when stop clean
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Should return true when exist a book in DB with the ispn informed")
    public void returnTrueWhenIsbnExist(){
        //scenario
        String isbn = "123";
        Object book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
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
}

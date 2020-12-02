package com.curty.libraryAPI.model.repository;

import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static com.curty.libraryAPI.model.repository.BookRepositoryTest.createNewBook;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository repository;

    @Test
    @DisplayName("Should verify if exist book loan not returned")
    public void existsByBookAndNotReturnedTest(){
        //scenario
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
        entityManager.persist(loan);

        //execution
        boolean exists = repository.existsByBookAndNotReturned(book);

        //verifications
        Assertions.assertThat(exists).isTrue();
    }
}

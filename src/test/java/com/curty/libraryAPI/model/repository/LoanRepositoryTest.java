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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private Loan createAndPersistLoan(Book book) {
        Loan loan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
        entityManager.persist(loan);
        return loan;
    }

    private Book createAndPersistBook() {
        Book book = createNewBook();
        entityManager.persist(book);
        return book;
    }

    @Test
    @DisplayName("Should verify if exist book loan not returned")
    public void existsByBookAndNotReturnedTest(){
        //scenario
        Book book = createAndPersistBook();

        Loan loan = createAndPersistLoan(book);

        //execution
        boolean exists = repository.existsByBookAndNotReturned(book);

        //verifications
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find a loan by isbn or customer")
    public void findByBookIsbnOrCustomerTest(){
        //scenario
        Book book = createAndPersistBook();

        Loan loan = createAndPersistLoan(book);
        //execution

        Page<Loan> result = repository
                .findByBookIsbnOrCustomer(
                        book.getIsbn(),
                        loan.getCustomer(),
                        PageRequest.of(0, 10)
                );

        //verification
        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getContent()).contains(loan);
    }
}

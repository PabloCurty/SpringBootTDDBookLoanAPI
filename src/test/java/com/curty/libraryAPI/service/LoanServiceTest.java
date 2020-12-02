package com.curty.libraryAPI.service;

import com.curty.libraryAPI.exception.BusinessException;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import com.curty.libraryAPI.model.repository.LoanRepository;
import com.curty.libraryAPI.service.impl.BookServiceImpl;
import com.curty.libraryAPI.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    private LoanService service;

    @MockBean
    private LoanRepository repository;

    @BeforeEach
    public void SetUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a loan")
    public void saveLoanTest(){
        //scenario
        Book book = Book.builder().id((long) 11).build();
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        //execution
        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);
        Loan loan = service.save(savingLoan);

        //verification
        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Should a bussiness error when try to save loan when book is already loaned")
    public void loanedBookSaveTest(){
        //scenario
        Book book = Book.builder().id((long) 11).build();
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        //execution
        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(savingLoan));

        //verification
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");
        Mockito.verify(repository, Mockito.never()).save(savingLoan);

    }
}

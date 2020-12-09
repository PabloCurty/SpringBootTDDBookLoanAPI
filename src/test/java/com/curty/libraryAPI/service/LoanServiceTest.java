package com.curty.libraryAPI.service;

import com.curty.libraryAPI.api.dto.LoanFilterDTO;
import com.curty.libraryAPI.exception.BusinessException;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import com.curty.libraryAPI.model.repository.LoanRepository;
import com.curty.libraryAPI.service.impl.BookServiceImpl;
import com.curty.libraryAPI.service.impl.LoanServiceImpl;
import lombok.Builder;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public static Loan createLoan(Book book) {
        return Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }

    public static Book createBook() {
        return Book.builder().id((long) 11).isbn("321").build();
    }

    @Test
    @DisplayName("Should save a loan")
    public void saveLoanTest(){
        //scenario
        Book book = createBook();
        Loan savingLoan = createLoan(book);

        Loan savedLoan = createLoan(book);

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
        Book book = createBook();
        Loan savingLoan = createLoan(book);

        //execution
        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(savingLoan));

        //verification
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");
        Mockito.verify(repository, Mockito.never()).save(savingLoan);

    }

    @Test
    @DisplayName("Should get details about the loan by Id")
    public void getLoanDetailsTest(){
        //scenario
        Long id = 11L;
        Loan loan = createLoan(createBook());
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execution
        Optional<Loan> result = service.getById(id);

        //verification
        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
        Assertions.assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(repository).findById(id);
    }

    @Test
    @DisplayName("Should update the loan")
    public void updateLoanTest(){
        //scenario
        Loan loan = createLoan(createBook());
        loan.setId(11L);
        loan.setReturned(true);

        Mockito.when(repository.save(loan)).thenReturn(loan);

        //execution
        Loan updateLoan = service.update(loan);

        //verifications
        Assertions.assertThat(updateLoan.getReturned()).isTrue();

        Mockito.verify(repository).save(loan);
    }

    @Test
    @DisplayName("Should filter properties of loans ")
    public void findLoanTest(){
        //scenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder()
                .customer("Fulano")
                .isbn("321")
                .build();
        Loan loan = createLoan(createBook());
        loan.setId(11L);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> list = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(list, pageRequest, 1);
        Mockito.when(repository.findByBookIsbnOrCustomer(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class))
        )
                .thenReturn(page);

        //execution
        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        //verifications
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(list);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}

package com.curty.libraryAPI.api.resource;

import com.curty.libraryAPI.api.dto.LoanDTO;
import com.curty.libraryAPI.api.dto.ReturnedLoanDTO;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import com.curty.libraryAPI.service.BookService;
import com.curty.libraryAPI.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private LoanService loanService;
    private BookService bookService;
    private ModelMapper modelMapper;

    public LoanController(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto){
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Book not found for passed isbn"));

        Loan entity = Loan.builder()
                        .book(book)
                        .customer(dto.getCustomer())
                        .loanDate(LocalDate.now())
                        .build();
        entity = loanService.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = loanService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.isReturned());
        loanService.update(loan);
    }

}

package com.curty.libraryAPI.service;

import com.curty.libraryAPI.api.dto.LoanFilterDTO;
import com.curty.libraryAPI.api.resource.BookController;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable);

    Page<Loan> getLoanByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}

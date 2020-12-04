package com.curty.libraryAPI.service;

import com.curty.libraryAPI.api.resource.BookController;
import com.curty.libraryAPI.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}

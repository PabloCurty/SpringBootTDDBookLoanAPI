package com.curty.libraryAPI.service.impl;

import com.curty.libraryAPI.exception.BusinessException;
import com.curty.libraryAPI.model.entity.Loan;
import com.curty.libraryAPI.model.repository.LoanRepository;
import com.curty.libraryAPI.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }
}

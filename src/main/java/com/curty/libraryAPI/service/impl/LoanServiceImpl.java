package com.curty.libraryAPI.service.impl;

import com.curty.libraryAPI.api.dto.LoanFilterDTO;
import com.curty.libraryAPI.exception.BusinessException;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import com.curty.libraryAPI.model.repository.LoanRepository;
import com.curty.libraryAPI.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

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

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable) {
        return repository
                .findByBookIsbnOrCustomer(
                        filterDTO.getIsbn(),
                        filterDTO.getCustomer(),
                        pageable
                );
    }

    @Override
    public Page<Loan> getLoanByBook(Book book, Pageable pageable) {
        //passando um pageable com último parametro de um query method, o spring data
        // já entende que a consulta é paginada, e já vai retornar uma página
        return repository.findByBook(book, pageable);
    }
}

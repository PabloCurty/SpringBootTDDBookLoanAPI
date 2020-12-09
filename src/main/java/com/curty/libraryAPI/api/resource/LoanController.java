package com.curty.libraryAPI.api.resource;

import com.curty.libraryAPI.api.dto.BookDOT;
import com.curty.libraryAPI.api.dto.LoanDTO;
import com.curty.libraryAPI.api.dto.LoanFilterDTO;
import com.curty.libraryAPI.api.dto.ReturnedLoanDTO;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import com.curty.libraryAPI.service.BookService;
import com.curty.libraryAPI.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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


    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest){
        //Loan filter = modelMapper.map(dto, Loan.class);
        Page<Loan> result = loanService.find(dto, pageRequest);
        List<LoanDTO> list = result.getContent()
                .stream()
                .map(entity -> {
                    Book book = new Book();
                    BookDOT bookDTO = modelMapper.map(book, BookDOT.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBookDOT(bookDTO);
                    return loanDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<LoanDTO>(list, pageRequest, result.getTotalElements());
    }

}

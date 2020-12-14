package com.curty.libraryAPI.api.resource;

import com.curty.libraryAPI.api.dto.BookDOT;
import com.curty.libraryAPI.api.dto.LoanDTO;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.model.entity.Loan;
import com.curty.libraryAPI.service.BookService;
import com.curty.libraryAPI.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a book")
    public BookDOT create(@RequestBody @Valid BookDOT dto){
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDOT.class);
    }

    @GetMapping("{id}")
    @ApiOperation("Find book details by id")
    public BookDOT get(@PathVariable Long id){
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDOT.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book successfully deleted")
    })
    public void  delete(@PathVariable Long id){
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);

    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book")
    public BookDOT update(@PathVariable Long id, BookDOT dto){
        return service.getById(id).map( book -> {

            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);
            return modelMapper.map(book, BookDOT.class);

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookDOT> find(BookDOT dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDOT> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDOT.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDOT>(list, pageRequest, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    @ApiOperation("Find Loans by Book")
    public Page<LoanDTO> findLoansByBook(@PathVariable Long id, Pageable pageable){
        Book book = service.getById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoanByBook(book, pageable);
        List<LoanDTO> content = result.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDOT bookDOT = modelMapper.map(loanBook, BookDOT.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBookDOT(bookDOT);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(content, pageable, result.getTotalElements());
    }


}

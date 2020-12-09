package com.curty.libraryAPI.api.dto;

import com.curty.libraryAPI.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private String id;
    private String isbn;
    private String customer;
    private BookDOT bookDOT;
}

package com.curty.libraryAPI.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

//@Data in addition to creating Getters and Setters also creates ToString and HashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Book {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String author;

    @Column
    private String isbn;

    //Lazy é preguiçoso, toda vez que busca livro não busca os emprestimos
    //Eager toda vez que busca o livro faz busca de todos os emprestimos
    // Eager -> pode gerar lentidão e consultas sem necessidade
    //Deafult de oneToMany já é Lazy, mas o código fica mais didático colocando
    @Column
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<Loan> loans;

}

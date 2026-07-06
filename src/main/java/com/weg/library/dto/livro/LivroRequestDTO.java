package com.weg.library.dto.livro;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.weg.library.model.Editora;
import com.weg.library.model.Autor;
import java.util.List;

public record LivroRequestDTO (
    String titulo,
    String isbn,
    BigDecimal preco,
    LocalDate dataPublicacao,
    String categoria,
    Editora editora,
    List<Autor> autores
){
    
}

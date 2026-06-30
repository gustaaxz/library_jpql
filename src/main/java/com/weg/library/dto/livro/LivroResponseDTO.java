package com.weg.library.dto.livro;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.weg.library.model.Editora;

public record LivroResponseDTO (
    Long id,
    String titulo,
    String isbn,
    BigDecimal preco,
    LocalDate dataPublicacao,
    String categoria,
    Editora editora
){
    
}

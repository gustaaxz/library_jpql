package com.weg.library.dto.livro;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LivroResponseDTO (
    Long id,
    String titulo,
    String isbn,
    BigDecimal preco,
    LocalDate dataPublicacao,
    String categoria
){
    
}

package com.weg.library.dto.livro;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.weg.library.dto.editora.EditoraResponseDTO;
import com.weg.library.dto.autor.AutorResponseDTO;
import java.util.List;

public record LivroResponseDTO (
    Long id,
    String titulo,
    String isbn,
    BigDecimal preco,
    LocalDate dataPublicacao,
    String categoria,
    EditoraResponseDTO editora,
    List<AutorResponseDTO> autores
){
    
}

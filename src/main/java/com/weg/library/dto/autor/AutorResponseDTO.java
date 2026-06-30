package com.weg.library.dto.autor;

import java.time.LocalDate;

public record AutorResponseDTO (
    Long id,
    String nome,
    String nacionalidade,
    LocalDate dataNascimento
){
    
}

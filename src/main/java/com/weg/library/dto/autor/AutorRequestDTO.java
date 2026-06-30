package com.weg.library.dto.autor;

import java.time.LocalDate;

public record AutorRequestDTO (
    String nome,
    String nacionalidade,
    LocalDate dataNascimento
){
    
}

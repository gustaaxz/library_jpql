package com.weg.library.dto.editora;

public record EditoraRequestDTO (
    String nome,
    String cnpj,
    String email,
    Long telefone
){
    
}

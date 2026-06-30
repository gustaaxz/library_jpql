package com.weg.library.dto.editora;

public record EditoraResponseDTO (
    Long id,
    String nome,
    String cnpj,
    String email,
    Long telefone
){
    
}

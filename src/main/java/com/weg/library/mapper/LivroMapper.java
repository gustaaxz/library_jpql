package com.weg.library.mapper;

import org.springframework.stereotype.Component;

import com.weg.library.dto.livro.LivroRequestDTO;
import com.weg.library.dto.livro.LivroResponseDTO;
import com.weg.library.model.Livro;

@Component
public class LivroMapper {
    public Livro toEntity(LivroRequestDTO requestDTO){
        return new Livro(
            requestDTO.titulo(),
            requestDTO.isbn(),
            requestDTO.preco(),
            requestDTO.dataPublicacao(),
            requestDTO.categoria(),
            requestDTO.editora()
        );
    }

    public LivroResponseDTO toResponse(Livro livro){
        return new LivroResponseDTO(
            livro.getId(),
            livro.getTitulo(),
            livro.getIsbn(),
            livro.getPreco(),
            livro.getDataPublicacao(),
            livro.getCategoria(),
            livro.getEditora()
        );
    }
}
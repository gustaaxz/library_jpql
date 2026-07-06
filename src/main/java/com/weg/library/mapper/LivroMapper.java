package com.weg.library.mapper;

import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

import com.weg.library.dto.livro.LivroRequestDTO;
import com.weg.library.dto.livro.LivroResponseDTO;
import com.weg.library.model.Livro;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LivroMapper {

    private final EditoraMapper editoraMapper;
    private final AutorMapper autorMapper;

    public Livro toEntity(LivroRequestDTO requestDTO){
        Livro livro = new Livro(
            requestDTO.titulo(),
            requestDTO.isbn(),
            requestDTO.preco(),
            requestDTO.dataPublicacao(),
            requestDTO.categoria(),
            requestDTO.editora()
        );
        
        livro.setAutores(requestDTO.autores());

        if (livro.getEditora() != null) {
            livro.getEditora().getLivros().add(livro);
        }
        
        return livro;
    }

    public LivroResponseDTO toResponse(Livro livro){
        return new LivroResponseDTO(
            livro.getId(),
            livro.getTitulo(),
            livro.getIsbn(),
            livro.getPreco(),
            livro.getDataPublicacao(),
            livro.getCategoria(),
            livro.getEditora() != null ? editoraMapper.toResponse(livro.getEditora()) : null,
            livro.getAutores() != null ? livro.getAutores().stream().map(autorMapper::toResponse).collect(Collectors.toList()) : null
        );
    }
}
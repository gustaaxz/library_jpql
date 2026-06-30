package com.weg.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.library.dto.livro.LivroRequestDTO;
import com.weg.library.dto.livro.LivroResponseDTO;
import com.weg.library.mapper.LivroMapper;
import com.weg.library.model.Livro;
import com.weg.library.repository.LivroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository repository;
    private final LivroMapper mapper;

    public LivroResponseDTO postLivro(LivroRequestDTO requestDTO){
        Livro livro = mapper.toEntity(requestDTO);
        repository.save(livro);
        return mapper.toResponse(livro);
    }

    public List<LivroResponseDTO> getAllBooks(){
        List<Livro> livros = repository.findAll();
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> getLivroByTitle(String titulo){
        List<Livro> livros = repository.buscarLivroPorNome(titulo);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
        
    }
}

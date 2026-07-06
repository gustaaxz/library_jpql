package com.weg.library.service;

import java.math.BigDecimal;
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

    public List<LivroResponseDTO> buscarPorCategoriaEPrecoMenorQue(String categoria, BigDecimal preco){
        List<Livro> livros = repository.buscarPorCategoriaEPrecoMenorQue(categoria, preco);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarPorPrecoEntre(BigDecimal min, BigDecimal max){
        List<Livro> livros = repository.buscarPorPrecoEntre(min, max);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarPorCategorias(List<String> categorias){
        List<Livro> livros = repository.buscarPorCategorias(categorias);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarLivrosSemIsbn(){
        List<Livro> livros = repository.buscarLivrosSemIsbn();
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarLivrosPorEditoraOrdenadoPorTitulo(String nomeEditora){
        List<Livro> livros = repository.buscarLivrosPorEditoraOrdenadoPorTitulo(nomeEditora);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public Long contarLivrosPorNacionalidadeDeAutor(String nacionalidade){
        return repository.contarLivrosPorNacionalidadeDeAutor(nacionalidade);
    }
}

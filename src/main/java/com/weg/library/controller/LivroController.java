package com.weg.library.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.weg.library.dto.livro.LivroRequestDTO;
import com.weg.library.dto.livro.LivroResponseDTO;
import com.weg.library.service.LivroService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/livro")
@AllArgsConstructor
public class LivroController {

    private final LivroService service;
    
    @PostMapping
    public LivroResponseDTO post(@RequestBody LivroRequestDTO requestDTO){
        try {
            return service.postLivro(requestDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping
    public List<LivroResponseDTO> getAllBooks(){
        try {
            return service.getAllBooks();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/{titulo}")
    public List<LivroResponseDTO> findLivroByTitle(@PathVariable String titulo){
        try {
            return service.getLivroByTitle(titulo);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public List<LivroResponseDTO> buscarPorCategoriaEPreco(@RequestParam String categoria, @RequestParam BigDecimal preco) {
        try {
            return service.buscarPorCategoriaEPrecoMenorQue(categoria, preco);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar-por-preco")
    public List<LivroResponseDTO> buscarPorPrecoEntre(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        try {
            return service.buscarPorPrecoEntre(min, max);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar-por-categorias")
    public List<LivroResponseDTO> buscarPorCategorias(@RequestParam List<String> categorias) {
        try {
            return service.buscarPorCategorias(categorias);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/sem-isbn")
    public List<LivroResponseDTO> buscarLivrosSemIsbn() {
        try {
            return service.buscarLivrosSemIsbn();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar-por-editora")
    public List<LivroResponseDTO> buscarLivrosPorEditoraOrdenadoPorTitulo(@RequestParam String editora) {
        try {
            return service.buscarLivrosPorEditoraOrdenadoPorTitulo(editora);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/contar-por-nacionalidade-autor")
    public Long contarLivrosPorNacionalidadeDeAutor(@RequestParam String nacionalidade) {
        try {
            return service.contarLivrosPorNacionalidadeDeAutor(nacionalidade);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

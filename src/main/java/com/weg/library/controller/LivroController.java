package com.weg.library.controller;

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
}

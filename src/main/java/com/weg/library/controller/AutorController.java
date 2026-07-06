package com.weg.library.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.weg.library.dto.autor.AutorRequestDTO;
import com.weg.library.dto.autor.AutorResponseDTO;
import com.weg.library.service.AutorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/autor")
@AllArgsConstructor
public class AutorController {

    private final AutorService service;

    @GetMapping("/buscar")
    // Exemplo de endereçamento pra utilizar "localhost:8080/autor/buscar?trecho=exemplo"
    public List<AutorResponseDTO> buscarAutoresPorNomeContendo(@RequestParam String trecho) {
        try {
            return service.buscarAutoresPorNomeContendo(trecho);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping
    // Exemplo de endereçamento: POST http://localhost:8080/autor
    public AutorResponseDTO post(@RequestBody AutorRequestDTO requestDTO) {
        try {
            return service.postAutor(requestDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping
    // Exemplo de endereçamento: GET http://localhost:8080/autor
    public List<AutorResponseDTO> getAll() {
        try {
            return service.buscarTodos();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    // Exemplo de endereçamento: GET http://localhost:8080/autor/1
    public AutorResponseDTO getById(@PathVariable Long id) {
        try {
            return service.buscarPorId(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    // Exemplo de endereçamento: PUT http://localhost:8080/autor/1
    public AutorResponseDTO update(@PathVariable Long id, @RequestBody AutorRequestDTO requestDTO) {
        try {
            return service.atualizarAutor(id, requestDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    // Exemplo de endereçamento: DELETE http://localhost:8080/autor/1
    public void delete(@PathVariable Long id) {
        try {
            service.deletarAutor(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
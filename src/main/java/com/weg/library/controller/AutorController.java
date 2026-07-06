package com.weg.library.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.weg.library.dto.autor.AutorResponseDTO;
import com.weg.library.service.AutorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/autor")
@AllArgsConstructor
public class AutorController {

    private final AutorService service;

    @GetMapping("/buscar")
    public List<AutorResponseDTO> buscarAutoresPorNomeContendo(@RequestParam String trecho) {
        try {
            return service.buscarAutoresPorNomeContendo(trecho);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
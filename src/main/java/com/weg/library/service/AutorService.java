package com.weg.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.library.dto.autor.AutorResponseDTO;
import com.weg.library.mapper.AutorMapper;
import com.weg.library.model.Autor;
import com.weg.library.repository.AutorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutorService {
    private final AutorRepository repository;
    private final AutorMapper mapper;

    public List<AutorResponseDTO> buscarAutoresPorNomeContendo(String trecho){
        List<Autor> autores = repository.buscarAutoresPorNomeContendo(trecho);
        return autores.stream()
                    .map(mapper::toResponse)
                    .toList();
    }
}

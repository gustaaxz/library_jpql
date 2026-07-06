package com.weg.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.library.dto.autor.AutorRequestDTO;
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

    public AutorResponseDTO postAutor(AutorRequestDTO requestDTO) {
        Autor autor = mapper.toEntity(requestDTO);
        repository.save(autor);
        return mapper.toResponse(autor);
    }

    public List<AutorResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public AutorResponseDTO buscarPorId(Long id) {
        Autor autor = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Autor não encontrado."));
            
        return mapper.toResponse(autor);
    }

    public AutorResponseDTO atualizarAutor(Long id, AutorRequestDTO requestDTO) {
        Autor autor = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Autor não encontrado."));

        autor.setNome(requestDTO.nome());
        autor.setNacionalidade(requestDTO.nacionalidade());
        autor.setDataNascimento(requestDTO.dataNascimento());
        repository.save(autor);
        return mapper.toResponse(autor);
    }

    public void deletarAutor(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Autor não encontrado.");
        }
        repository.deleteById(id);
    }
}

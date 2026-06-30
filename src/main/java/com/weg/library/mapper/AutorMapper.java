package com.weg.library.mapper;

import org.springframework.stereotype.Component;

import com.weg.library.dto.autor.AutorRequestDTO;
import com.weg.library.dto.autor.AutorResponseDTO;
import com.weg.library.model.Autor;

@Component
public class AutorMapper {
    public Autor toEntity(AutorRequestDTO requestDTO){
        return new Autor(
            requestDTO.nome(),
            requestDTO.nacionalidade(),
            requestDTO.dataNascimento()
        );
    }

    public AutorResponseDTO toResponse(Autor autor){
        return new AutorResponseDTO(
            autor.getId(),
            autor.getNome(),
            autor.getNacionalidade(),
            autor.getDataNascimento()
        );
    }
}

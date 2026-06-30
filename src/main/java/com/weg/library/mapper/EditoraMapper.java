package com.weg.library.mapper;

import org.springframework.stereotype.Component;

import com.weg.library.dto.editora.EditoraRequestDTO;
import com.weg.library.dto.editora.EditoraResponseDTO;
import com.weg.library.model.Editora;

@Component
public class EditoraMapper {
    public Editora toEntity(EditoraRequestDTO requestDTO){
        return new Editora(
            requestDTO.nome(),
            requestDTO.email(),
            requestDTO.cnpj(),
            requestDTO.telefone()
        );
    }

    public EditoraResponseDTO toResponse(Editora editora){
        return new EditoraResponseDTO(
            editora.getId(),
            editora.getNome(),
            editora.getCnpj(),
            editora.getEmail(),
            editora.getTelefone()
        );
    }
}

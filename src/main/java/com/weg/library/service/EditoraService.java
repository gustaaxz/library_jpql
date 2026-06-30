package com.weg.library.service;

import org.springframework.stereotype.Service;

import com.weg.library.mapper.EditoraMapper;
import com.weg.library.repository.EditoraRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditoraService {
    private final EditoraRepository repository;
    private final EditoraMapper mapper;
}

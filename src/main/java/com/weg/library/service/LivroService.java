package com.weg.library.service;

import org.springframework.stereotype.Service;

import com.weg.library.mapper.LivroMapper;
import com.weg.library.repository.LivroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository repository;
    private final LivroMapper mapper;
}

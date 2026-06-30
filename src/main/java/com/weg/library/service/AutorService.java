package com.weg.library.service;

import org.springframework.stereotype.Service;

import com.weg.library.mapper.AutorMapper;
import com.weg.library.repository.AutorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutorService {
    private final AutorRepository repository;
    private final AutorMapper mapper;
}

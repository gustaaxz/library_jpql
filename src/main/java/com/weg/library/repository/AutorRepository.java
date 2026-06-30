package com.weg.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.library.model.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    
}

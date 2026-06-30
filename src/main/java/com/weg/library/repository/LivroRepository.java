package com.weg.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.library.model.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>{
    
}

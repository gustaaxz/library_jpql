package com.weg.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.library.model.Editora;

@Repository
public interface EditoraRepository extends JpaRepository<Editora, Long>{
    
}

package com.weg.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weg.library.model.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    
    @Query("""
            SELECT a
            FROM Autor a
            WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :trecho, '%'))
            """)
    List<Autor> buscarAutoresPorNomeContendo(@Param("trecho") String trecho);
}

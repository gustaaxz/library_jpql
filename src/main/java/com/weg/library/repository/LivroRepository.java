package com.weg.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weg.library.model.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>{
    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.titulo = :titulo
            """)

    List<Livro> buscarLivroPorNome(@Param("titulo") String titulo);
}

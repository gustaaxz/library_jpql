package com.weg.library.repository;

import java.math.BigDecimal;
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

    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.categoria = :categoria AND l.preco < :preco
            """)
    List<Livro> buscarPorCategoriaEPrecoMenorQue(@Param("categoria") String categoria, @Param("preco") BigDecimal preco);

    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.preco BETWEEN :min AND :max
            """)
    List<Livro> buscarPorPrecoEntre(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.categoria IN :categorias
            """)
    List<Livro> buscarPorCategorias(@Param("categorias") List<String> categorias);

    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.isbn IS NULL
            """)
    List<Livro> buscarLivrosSemIsbn();

    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.editora.nome = :nomeEditora
            ORDER BY l.titulo ASC
            """)
    List<Livro> buscarLivrosPorEditoraOrdenadoPorTitulo(@Param("nomeEditora") String nomeEditora);

    @Query("""
            SELECT COUNT(DISTINCT l)
            FROM Livro l
            JOIN l.autores a
            WHERE a.nacionalidade = :nacionalidade
            """)
    Long contarLivrosPorNacionalidadeDeAutor(@Param("nacionalidade") String nacionalidade);
}

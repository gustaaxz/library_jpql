package com.weg.library.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weg.library.model.Livro;
import com.weg.library.projection.LivroMinimoProjection;
import com.weg.library.dto.editora.EstatisticasEditoraDTO;

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

    @Query("""
            SELECT l.titulo
            FROM Livro l
            WHERE l.categoria = :categoria
            """)
    List<String> buscarTitulosPorCategoria(@Param("categoria") String categoria);

    @Query("""
            SELECT l
            FROM Livro l
            JOIN l.autores a
            WHERE a.nome = :nomeAutor
            """)
    List<Livro> buscarLivrosPorNomeAutor(@Param("nomeAutor") String nomeAutor);

    @Query("""
            SELECT l
            FROM Livro l
            JOIN FETCH l.autores
            """)
    List<Livro> buscarLivrosComAutores();

    @Query("""
            SELECT AVG(l.preco)
            FROM Livro l
            WHERE l.editora.nome = :nomeEditora
            """)
    Double calcularMediaPrecoPorEditora(@Param("nomeEditora") String nomeEditora);

    @Query("""
            SELECT l
            FROM Livro l
            WHERE l.preco > (SELECT AVG(l2.preco) FROM Livro l2)
            """)
    List<Livro> buscarLivrosAcimaDaMedia();

    @NativeQuery(value = """
            SELECT *
            FROM livro
            WHERE YEAR(data_publicacao) = 2023
            """)
    List<Livro> buscarLivrosPorAno2023();

    @NativeQuery(value = """
            SELECT l.*
            FROM livro l
            JOIN livro_autores la ON l.id = la.id_livro
            JOIN autor a ON a.id = la.id_autor
            WHERE a.nacionalidade = 'Brasileiro'
            """)
    List<Livro> buscarLivrosDeAutoresBrasileirosNative();

    @NativeQuery(value = """
            SELECT *
            FROM livro
            WHERE LOWER(categoria) = LOWER(:categoria)
            """)
    List<Livro> buscarLivrosPorCategoriaIgnorandoCaixaNative(@Param("categoria") String categoria);

    @Query("""
            SELECT l.titulo AS titulo, l.preco AS preco
            FROM Livro l
            """)
    List<LivroMinimoProjection> buscarLivrosComProjecao();

    @Query("""
            SELECT new com.weg.library.dto.editora.EstatisticasEditoraDTO(e.nome, COUNT(l))
            FROM Editora e
            LEFT JOIN e.livros l
            GROUP BY e.nome
            """)
    List<EstatisticasEditoraDTO> buscarEstatisticasEditora();

    @Query(value = """
            SELECT titulo AS titulo, preco AS preco
            FROM livro
            """, nativeQuery = true)
    List<LivroMinimoProjection> buscarLivrosComProjecaoNativa();

    <T> List<T> findByCategoria(String categoria, Class<T> type);
}

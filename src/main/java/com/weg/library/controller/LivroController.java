package com.weg.library.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.weg.library.dto.livro.LivroRequestDTO;
import com.weg.library.dto.livro.LivroResponseDTO;
import com.weg.library.projection.LivroMinimoProjection;
import com.weg.library.projection.LivroTituloProjection;
import com.weg.library.dto.editora.EstatisticasEditoraDTO;
import com.weg.library.model.Livro;
import org.springframework.http.ResponseEntity;
import com.weg.library.service.LivroService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/livro")
@AllArgsConstructor
public class LivroController {

    private final LivroService service;
    
    @PostMapping
    // Exemplo de endereçamento: POST http://localhost:8080/livro
    public LivroResponseDTO post(@RequestBody LivroRequestDTO requestDTO){
        try {
            return service.postLivro(requestDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping
    // Exemplo de endereçamento: GET http://localhost:8080/livro
    public List<LivroResponseDTO> getAllBooks(){
        try {
            return service.getAllBooks();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/{titulo}")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/Senhor dos Aneis
    public List<LivroResponseDTO> findLivroByTitle(@PathVariable String titulo){
        try {
            return service.getLivroByTitle(titulo);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/buscar?categoria=Terror&preco=50
    public List<LivroResponseDTO> buscarPorCategoriaEPreco(@RequestParam String categoria, @RequestParam BigDecimal preco) {
        try {
            return service.buscarPorCategoriaEPrecoMenorQue(categoria, preco);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar-por-preco")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/buscar-por-preco?min=20&max=100
    public List<LivroResponseDTO> buscarPorPrecoEntre(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        try {
            return service.buscarPorPrecoEntre(min, max);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar-por-categorias")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/buscar-por-categorias?categorias=Ficcao,Terror
    public List<LivroResponseDTO> buscarPorCategorias(@RequestParam List<String> categorias) {
        try {
            return service.buscarPorCategorias(categorias);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/sem-isbn")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/sem-isbn
    public List<LivroResponseDTO> buscarLivrosSemIsbn() {
        try {
            return service.buscarLivrosSemIsbn();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar-por-editora")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/buscar-por-editora?editora=Rocco
    public List<LivroResponseDTO> buscarLivrosPorEditoraOrdenadoPorTitulo(@RequestParam String editora) {
        try {
            return service.buscarLivrosPorEditoraOrdenadoPorTitulo(editora);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/contar-por-nacionalidade-autor")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/contar-por-nacionalidade-autor?nacionalidade=Brasileiro
    public Long contarLivrosPorNacionalidadeDeAutor(@RequestParam String nacionalidade) {
        try {
            return service.contarLivrosPorNacionalidadeDeAutor(nacionalidade);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/titulos-por-categoria")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/titulos-por-categoria?categoria=Fantasia
    public List<String> buscarTitulosPorCategoria(@RequestParam String categoria) {
        try {
            return service.buscarTitulosPorCategoria(categoria);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/buscar-por-autor")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/buscar-por-autor?nomeAutor=Tolkien
    public List<LivroResponseDTO> buscarLivrosPorNomeAutor(@RequestParam String nomeAutor) {
        try {
            return service.buscarLivrosPorNomeAutor(nomeAutor);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/com-autores")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/com-autores
    public List<LivroResponseDTO> buscarLivrosComAutores() {
        try {
            return service.buscarLivrosComAutores();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/media-preco-por-editora")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/media-preco-por-editora?editora=Rocco
    public Double calcularMediaPrecoPorEditora(@RequestParam String editora) {
        try {
            return service.calcularMediaPrecoPorEditora(editora);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/acima-da-media")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/acima-da-media
    public List<LivroResponseDTO> buscarLivrosAcimaDaMedia() {
        try {
            return service.buscarLivrosAcimaDaMedia();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/ano-2023")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/ano-2023
    public List<LivroResponseDTO> buscarLivrosPorAno2023() {
        try {
            return service.buscarLivrosPorAno2023();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/autores-brasileiros-nativo")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/autores-brasileiros-nativo
    public List<LivroResponseDTO> buscarLivrosDeAutoresBrasileirosNative() {
        try {
            return service.buscarLivrosDeAutoresBrasileirosNative();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/categoria-ignorando-caixa")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/categoria-ignorando-caixa?categoria=TeRrOr
    public List<LivroResponseDTO> buscarLivrosPorCategoriaIgnorandoCaixaNative(@RequestParam String categoria) {
        try {
            return service.buscarLivrosPorCategoriaIgnorandoCaixaNative(categoria);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/projecao-minima")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/projecao-minima
    public List<LivroMinimoProjection> buscarLivrosComProjecao() {
        try {
            return service.buscarLivrosComProjecao();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/estatisticas-editora")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/estatisticas-editora
    public List<EstatisticasEditoraDTO> buscarEstatisticasEditora() {
        try {
            return service.buscarEstatisticasEditora();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/projecao-nativa")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/projecao-nativa
    public List<LivroMinimoProjection> buscarLivrosComProjecaoNativa() {
        try {
            return service.buscarLivrosComProjecaoNativa();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/projecao-dinamica")
    // Exemplo de endereçamento: GET http://localhost:8080/livro/projecao-dinamica?categoria=Ficcao&tipo=minima
    public ResponseEntity<?> buscarLivrosComProjecaoDinamica(@RequestParam String categoria, @RequestParam String tipo) {
        try {
            if ("minima".equalsIgnoreCase(tipo)) {
                return ResponseEntity.ok(service.buscarLivrosPorCategoriaComProjecaoDinamica(categoria, LivroMinimoProjection.class));
            } else if ("titulo".equalsIgnoreCase(tipo)) {
                return ResponseEntity.ok(service.buscarLivrosPorCategoriaComProjecaoDinamica(categoria, LivroTituloProjection.class));
            } else {
                return ResponseEntity.ok(service.buscarLivrosPorCategoriaComProjecaoDinamica(categoria, Livro.class));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

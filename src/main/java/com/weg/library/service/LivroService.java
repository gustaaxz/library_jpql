package com.weg.library.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.library.dto.livro.LivroRequestDTO;
import com.weg.library.dto.livro.LivroResponseDTO;
import com.weg.library.mapper.LivroMapper;
import com.weg.library.model.Autor;
import com.weg.library.model.Livro;
import com.weg.library.projection.LivroMinimoProjection;
import com.weg.library.dto.editora.EstatisticasEditoraDTO;
import com.weg.library.repository.LivroRepository;
import com.weg.library.repository.AutorRepository;
import com.weg.library.repository.EditoraRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository repository;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;
    private final LivroMapper mapper;

    public LivroResponseDTO postLivro(LivroRequestDTO requestDTO){
        Livro livro = mapper.toEntity(requestDTO);
        
        // Se a editora vier com ID, ou com CNPJ, busca do banco para evitar duplicar via CascadeType.PERSIST
        if (livro.getEditora() != null) {
            if (livro.getEditora().getId() != null) {
                livro.setEditora(editoraRepository.findById(livro.getEditora().getId()).orElse(livro.getEditora()));
            } else if (livro.getEditora().getCnpj() != null && !livro.getEditora().getCnpj().isBlank()) {
                editoraRepository.findByCnpj(livro.getEditora().getCnpj()).ifPresent(livro::setEditora);
            }
        }

        // Busca os autores completos no banco para não retornar os nulos do JSON, ou mantém caso sejam autores novos
        if (livro.getAutores() != null && !livro.getAutores().isEmpty()) {
            List<Autor> autoresFinais = new java.util.ArrayList<>();
            for (Autor autor : livro.getAutores()) {
                if (autor.getId() != null) {
                    autoresFinais.add(autorRepository.findById(autor.getId())
                        .orElseThrow(() -> new RuntimeException("Autor com ID " + autor.getId() + " não encontrado no banco de dados.")));
                } else {
                    autoresFinais.add(autor);
                }
            }
            livro.setAutores(autoresFinais);
        }

        livro = repository.save(livro);
        return mapper.toResponse(livro);
    }

    public List<LivroResponseDTO> getAllBooks(){
        List<Livro> livros = repository.findAll();
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> getLivroByTitle(String titulo){
        List<Livro> livros = repository.buscarLivroPorNome(titulo);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarPorCategoriaEPrecoMenorQue(String categoria, BigDecimal preco){
        List<Livro> livros = repository.buscarPorCategoriaEPrecoMenorQue(categoria, preco);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarPorPrecoEntre(BigDecimal min, BigDecimal max){
        List<Livro> livros = repository.buscarPorPrecoEntre(min, max);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarPorCategorias(List<String> categorias){
        List<Livro> livros = repository.buscarPorCategorias(categorias);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarLivrosSemIsbn(){
        List<Livro> livros = repository.buscarLivrosSemIsbn();
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarLivrosPorEditoraOrdenadoPorTitulo(String nomeEditora){
        List<Livro> livros = repository.buscarLivrosPorEditoraOrdenadoPorTitulo(nomeEditora);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public Long contarLivrosPorNacionalidadeDeAutor(String nacionalidade){
        return repository.contarLivrosPorNacionalidadeDeAutor(nacionalidade);
    }

    public List<String> buscarTitulosPorCategoria(String categoria){
        return repository.buscarTitulosPorCategoria(categoria);
    }

    public List<LivroResponseDTO> buscarLivrosPorNomeAutor(String nomeAutor){
        List<Livro> livros = repository.buscarLivrosPorNomeAutor(nomeAutor);
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public List<LivroResponseDTO> buscarLivrosComAutores(){
        List<Livro> livros = repository.buscarLivrosComAutores();
        return livros.stream()
                    .map(mapper::toResponse)
                    .toList();
    }

    public Double calcularMediaPrecoPorEditora(String nomeEditora) {
        return repository.calcularMediaPrecoPorEditora(nomeEditora);
    }

    public List<LivroResponseDTO> buscarLivrosAcimaDaMedia() {
        return repository.buscarLivrosAcimaDaMedia().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<LivroResponseDTO> buscarLivrosPorAno2023() {
        return repository.buscarLivrosPorAno2023().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<LivroResponseDTO> buscarLivrosDeAutoresBrasileirosNative() {
        return repository.buscarLivrosDeAutoresBrasileirosNative().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<LivroResponseDTO> buscarLivrosPorCategoriaIgnorandoCaixaNative(String categoria) {
        return repository.buscarLivrosPorCategoriaIgnorandoCaixaNative(categoria).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<LivroMinimoProjection> buscarLivrosComProjecao() {
        return repository.buscarLivrosComProjecao();
    }

    public List<EstatisticasEditoraDTO> buscarEstatisticasEditora() {
        return repository.buscarEstatisticasEditora();
    }

    public List<LivroMinimoProjection> buscarLivrosComProjecaoNativa() {
        return repository.buscarLivrosComProjecaoNativa();
    }

    public <T> List<T> buscarLivrosPorCategoriaComProjecaoDinamica(String categoria, Class<T> type) {
        return repository.findByCategoria(categoria, type);
    }
}

# Implementação: Busca de Livros por Editora com Ordenação (OrderBy)

Este documento explica como listar todos os livros vinculados a uma Editora e, ao mesmo tempo, ordená-los pelo Título em ordem alfabética (de A a Z).

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT l
        FROM Livro l
        WHERE l.editora.nome = :nomeEditora
        ORDER BY l.titulo ASC
        """)
List<Livro> buscarLivrosPorEditoraOrdenadoPorTitulo(@Param("nomeEditora") String nomeEditora);
```

### Explicação Técnica:
* **Relacionamentos (Join Implícito)**: Como a entidade `Livro` possui o atributo `editora`, nós não precisamos fazer o "JOIN" manualmente como no SQL puro. O Hibernate entende o caminho `l.editora.nome` e gera o SQL com os `JOINs` correspondentes por debaixo dos panos.
* **`ORDER BY l.titulo`**: O comando `ORDER BY` é utilizado para classificar o resultado por uma ou mais colunas, neste caso, a coluna título da entidade Livro.
* **`ASC`**: Significa *Ascending* (Ascendente). Ou seja, garante que a classificação seja feita de A-Z. (Se fosse de Z-A, utilizaríamos `DESC`).

---

## 2. Camada de Serviço (`LivroService.java`)

```java
import java.util.List;
// ...

public List<LivroResponseDTO> buscarLivrosPorEditoraOrdenadoPorTitulo(String nomeEditora){
    List<Livro> livros = repository.buscarLivrosPorEditoraOrdenadoPorTitulo(nomeEditora);
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* Repassamos a string `nomeEditora` para a consulta no banco.
* O mapeamento e filtragem de Entidade para DTO continua ocorrendo normalmente na camada de serviço. Como o banco de dados já nos devolveu a lista ordenada (`ORDER BY`), a `List<Livro>` já vem pronta. Nós a mapeamos e ela mantém a ordem original.

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/buscar-por-editora")
public List<LivroResponseDTO> buscarLivrosPorEditoraOrdenadoPorTitulo(@RequestParam String editora) {
    try {
        return service.buscarLivrosPorEditoraOrdenadoPorTitulo(editora);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* **`@GetMapping("/buscar-por-editora")`**: Adicionamos o endpoint para fazer o filtro.
* **`@RequestParam String editora`**: O usuário enviará o nome da editora via query string (ex: `?editora=Rocco`).

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/buscar-por-editora?editora=Rocco`

Esta requisição buscará todos os livros da editora "Rocco" e os listará organizados por ordem alfabética do título.

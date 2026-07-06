# Implementação: Busca de Livros por Título Exato

Este documento explica a implementação da funcionalidade de busca de livros onde o título informado deve ser idêntico ao cadastrado no banco de dados.

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT l
        FROM Livro l
        WHERE l.titulo = :titulo
        """)
List<Livro> buscarLivroPorNome(@Param("titulo") String titulo);
```

### Explicação Técnica:
* **`@Query`**: Permite definir uma consulta customizada usando a sintaxe do JPA (JPQL).
* **`WHERE l.titulo = :titulo`**: Filtra exatamente os livros cujo título seja idêntico ao parâmetro enviado na requisição.
* **`@Param("titulo")`**: Mapeia o parâmetro `:titulo` da query com a variável `String titulo` do método.

---

## 2. Camada de Serviço (`LivroService.java`)

```java
public List<LivroResponseDTO> getLivroByTitle(String titulo){
    List<Livro> livros = repository.buscarLivroPorNome(titulo);
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* O repositório devolve uma lista de entidades `Livro`.
* Utilizamos a API de Streams (`.stream().map().toList()`) para converter cada entidade para um `LivroResponseDTO`, devolvendo os dados prontos para visualização e protegendo as informações de banco de dados.

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// ...

@GetMapping("/{titulo}")
public List<LivroResponseDTO> findLivroByTitle(@PathVariable String titulo){
    try {
        return service.getLivroByTitle(titulo);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* **`@GetMapping("/{titulo}")`**: Mapeia a URL com uma variável diretamente no caminho (ex: `/livro/Hobbit`). 
* **`@PathVariable`**: É a anotação que extrai o valor de dentro das chaves na URL (`{titulo}`) e joga para a variável Java correspondente. Esse padrão é muito usado no REST para encontrar recursos únicos.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/Harry Potter`

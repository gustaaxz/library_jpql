# Implementação: Solucionando o Problema N+1 (JOIN FETCH)

O problema do **N+1** é um dos piores inimigos de desempenho no JPA. Este documento explora como utilizar a poderosa cláusula `JOIN FETCH` do JPQL para forçar o banco a carregar as associações em uma única requisição.

O cenário implementado é: **Listar os livros da biblioteca já trazendo a lista de Autores pré-carregada, sem disparar novas queries no banco.**

## 1. O que é o Problema N+1?

Imagine que você busca 10 livros. O JPA dispara **1** Query (`SELECT * FROM livro`).
Na hora de transformar esses livros em DTO, o código pede os autores do livro 1. Como autores geralmente são configurados com *Lazy Loading* (carregamento preguiçoso), o JPA dispara **mais 1** Query para pegar os autores do livro 1. Depois **mais 1** Query para o livro 2... e assim por diante.
No final, para buscar os autores de 10 livros, ele disparou **1 + 10 (N) Queries = 11 Queries**. Em um cenário de 1000 livros, o seu banco receberia 1001 conexões!

## 2. Camada de Repositório (`LivroRepository.java`)

A solução no JPQL é extremamente simples: adicione a palavra `FETCH` ao lado do `JOIN`.

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;

// ...

@Query("""
        SELECT l
        FROM Livro l
        JOIN FETCH l.autores
        """)
List<Livro> buscarLivrosComAutores();
```

### Explicação Técnica:
* **`JOIN FETCH`**: Enquanto o `JOIN` normal (que usamos na query anterior) serve apenas para *filtrar* os dados (no comando WHERE), o `JOIN FETCH` serve para *carregar* os dados. Ele diz ao Hibernate: "Gere um grande `INNER JOIN` no SQL e já traga as informações da tabela Autor junto com os Livros dentro da mesma sacola".
* Com apenas essa linha de código, o Hibernate dispara **apenas 1 Query**, independentemente se a resposta trouxer 10, 1000 ou 1 milhão de livros.

---

## 3. Camada de Serviço (`LivroService.java`)

```java
import java.util.List;
// ...

public List<LivroResponseDTO> buscarLivrosComAutores(){
    List<Livro> livros = repository.buscarLivrosComAutores();
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* Quando o `mapper::toResponse` tentar acessar a propriedade `.getAutores()` lá do Livro para converter para o DTO, o JPA **não** disparará uma nova query para o banco de dados. Os autores já estarão alocados na memória do Java perfeitamente devido ao `FETCH`.

---

## 4. Camada de Controle/API (`LivroController.java`)

```java
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

// ...

@GetMapping("/com-autores")
public List<LivroResponseDTO> buscarLivrosComAutores() {
    try {
        return service.buscarLivrosComAutores();
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* Um endpoint `GET` direto para acessar o método. 

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/com-autores`

Se você tiver o log do SQL habilitado (`spring.jpa.show-sql=true`), verá que apenas **uma única Query** gigante com vários JOINs será plotada no seu terminal, e os objetos de Autores já virão completos dentro do JSON da resposta.

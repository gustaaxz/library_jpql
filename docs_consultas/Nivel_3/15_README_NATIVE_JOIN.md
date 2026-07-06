# Implementação: Native Query - JOIN Nativo

Continuando o **Nível 3**, mostraremos a grande diferença entre os JOINS do JPQL (Nível 2) e os JOINS nativos.

O cenário implementado é: **Realizar um join nativo entre livro, livro_autores e autor para retornar livros de autores brasileiros.**

## 1. Camada de Repositório (`LivroRepository.java`)

```java
@Query(value = """
        SELECT l.*
        FROM livro l
        JOIN livro_autores la ON l.id = la.id_livro
        JOIN autor a ON a.id = la.id_autor
        WHERE a.nacionalidade = 'Brasileiro'
        """, nativeQuery = true)
List<Livro> buscarLivrosDeAutoresBrasileirosNative();
```

### Explicação Técnica:
* Diferente do JPQL, onde nós apenas mandávamos um `JOIN l.autores`, no SQL puro o Banco de Dados não sabe que essas tabelas são mapeadas. 
* Portanto, nós temos que ser totalmente explícitos sobre **todas as tabelas envolvidas**, incluindo tabelas intermediárias (de amarração) como a `livro_autores`.
* Nós temos que dizer literalmente qual coluna é igual a qual coluna (ex: `ON l.id = la.id_livro`). É mais trabalhoso e mais suscetível a erros de digitação.

---

## 2. Camada de Serviço e Controller

* **Endpoint:** `GET /livro/autores-brasileiros-nativo`

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/autores-brasileiros-nativo`

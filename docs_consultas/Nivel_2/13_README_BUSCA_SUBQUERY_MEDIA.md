# Implementação: Subquery com Funções de Agregação

Para finalizar o **Nível 2** de JPQL, nós vamos unir dois mundos: Funções de agregação e Subqueries. 

O cenário implementado é: **Buscar todos os livros que custam mais caro que a média geral de preços da biblioteca.**

## 1. Camada de Repositório (`LivroRepository.java`)

```java
@Query("""
        SELECT l
        FROM Livro l
        WHERE l.preco > (SELECT AVG(l2.preco) FROM Livro l2)
        """)
List<Livro> buscarLivrosAcimaDaMedia();
```

### Explicação Técnica:
* **`(SELECT AVG(l2.preco) FROM Livro l2)`**: Esta é a nossa *Subquery*. É uma consulta dentro da consulta principal. Perceba que utilizamos o alias `l2` para não confundir o JPQL com o `l` da consulta principal.
* O banco de dados vai primeiro rodar a consulta de dentro (calcular a média geral), obter o número resultante, e então repassar esse número para a consulta de fora (`l.preco > X`).
* Isso permite uma consulta dinâmica extremamente poderosa sem precisar ir e voltar do Java duas vezes (uma vez pra pegar a média e outra vez pra buscar os livros).

---

## 2. Camada de Serviço e Controller

Foram criados normalmente para repassar a `List<LivroResponseDTO>`.

* **Endpoint:** `GET /livro/acima-da-media`

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/acima-da-media`

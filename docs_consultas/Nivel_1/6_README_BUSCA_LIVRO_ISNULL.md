# Implementação: Busca de Livros Sem ISBN Cadastrado (IsNull)

Este documento explica como implementar uma busca por registros onde um determinado campo não foi preenchido, ou seja, é Nulo no banco de dados. O cenário escolhido é encontrar livros que não possuem um número de ISBN cadastrado.

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;

// ...

@Query("""
        SELECT l
        FROM Livro l
        WHERE l.isbn IS NULL
        """)
List<Livro> buscarLivrosSemIsbn();
```

### Explicação Técnica:
* **`IS NULL`**: O comando `IS NULL` do JPQL verifica se a coluna no banco de dados está vazia (com o valor NULL literal). 
* **Ausência de `@Param`**: Repare que como não precisamos passar nenhum valor dinâmico para a Query (já que queremos comparar com um valor fixo nulo no banco), nós não utilizamos parâmetros na anotação ou na assinatura do método.

*Dica: Caso você precisasse do exato oposto (ex: "buscar apenas livros que JÁ possuam o ISBN"), você utilizaria a cláusula `IS NOT NULL` na Query.*

---

## 2. Camada de Serviço (`LivroService.java`)

```java
import java.util.List;
// ...

public List<LivroResponseDTO> buscarLivrosSemIsbn(){
    List<Livro> livros = repository.buscarLivrosSemIsbn();
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* Um método limpo, sem receber nenhum parâmetro, que apenas aciona o banco para recuperar os livros que não têm ISBN e os mapeia para o objeto de transferência de resposta (`LivroResponseDTO`), garantindo o isolamento da Entidade.

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

// ...

@GetMapping("/sem-isbn")
public List<LivroResponseDTO> buscarLivrosSemIsbn() {
    try {
        return service.buscarLivrosSemIsbn();
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* **`@GetMapping("/sem-isbn")`**: O endpoint também não necessita de parâmetros (nem de rota, nem de *Query String*). Qualquer um que fizer uma requisição a este endereço já disparará diretamente a regra configurada na Query para pegar todos sem ISBN.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/sem-isbn`

Uma simples chamada para esta URL, e todos os livros em pendência de ISBN aparecerão listados no JSON.

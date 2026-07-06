# Implementação: Busca de Livros por Intervalo de Preço (Between)

Este documento detalha como foi implementada a funcionalidade para buscar livros que estejam dentro de uma faixa de preço, utilizando um valor mínimo e um máximo.

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT l
        FROM Livro l
        WHERE l.preco BETWEEN :min AND :max
        """)
List<Livro> buscarPorPrecoEntre(
        @Param("min") BigDecimal min, 
        @Param("max") BigDecimal max
);
```

### Explicação Técnica:
* **`BETWEEN :min AND :max`**: O comando `BETWEEN` do SQL (e do JPQL) funciona como um intervalo inclusivo. Ou seja, ele vai retornar todos os livros cujo atributo `preco` seja maior ou igual a `:min` **E** menor ou igual a `:max`.
* **`@Param(...)`**: Relaciona os nomes descritivos da query com os parâmetros recebidos.

---

## 2. Camada de Serviço (`LivroService.java`)

```java
import java.math.BigDecimal;

// ...

public List<LivroResponseDTO> buscarPorPrecoEntre(BigDecimal min, BigDecimal max){
    List<Livro> livros = repository.buscarPorPrecoEntre(min, max);
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* Repassa a requisição para o `LivroRepository`.
* Utiliza `.stream().map(mapper::toResponse).toList()` para percorrer a lista original do banco de dados e transformar no formato idealizado na aplicação (`LivroResponseDTO`).

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/buscar-por-preco")
public List<LivroResponseDTO> buscarPorPrecoEntre(
        @RequestParam BigDecimal min, 
        @RequestParam BigDecimal max) {
    try {
        return service.buscarPorPrecoEntre(min, max);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* **`@GetMapping("/buscar-por-preco")`**: Cria uma rota específica para não colidir com a outra de categorias.
* **`@RequestParam`**: Extrai os dois filtros passados via Query String (`?min=X&max=Y`). Por ser um filtro com mais de uma variável onde não temos um recurso específico (e sim uma lista), usar parâmetros de query é a recomendação oficial das arquiteturas REST.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/buscar-por-preco?min=20&max=100`

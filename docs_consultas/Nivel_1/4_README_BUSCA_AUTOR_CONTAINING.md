# Implementação: Busca de Autores por Nome Contendo uma Palavra (Containing / Case Insensitive)

Este documento explica passo a passo a implementação da funcionalidade de busca de Autores onde o nome do autor contenha uma determinada sequência de caracteres ignorando maiúsculas e minúsculas (Case Insensitive).

## 1. Camada de Repositório (`AutorRepository.java`)

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT a
        FROM Autor a
        WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :trecho, '%'))
        """)
List<Autor> buscarAutoresPorNomeContendo(@Param("trecho") String trecho);
```

### Explicação Técnica:
* **`LIKE`**: O operador `LIKE` no SQL permite buscar padrões de texto. 
* **`%` e `CONCAT`**: Os caracteres `%` funcionam como "curingas". O comando `CONCAT('%', :trecho, '%')` cria a string final. Por exemplo, se passarmos "tolkien", vira `%tolkien%`, que significa que pode ter qualquer texto antes e qualquer texto depois da palavra "tolkien".
* **`LOWER(...)`**: O uso da função `LOWER` em ambos os lados transforma tanto o registro no banco quanto o texto pesquisado em minúsculo antes de comparar. Isso garante que a busca seja **Case Insensitive**, ou seja, pesquisar por "Tolkien", "TOLKIEN" ou "tolkien" vai retornar o mesmo resultado.

---

## 2. Camada de Serviço (`AutorService.java`)

```java
import java.util.List;
import com.weg.library.dto.autor.AutorResponseDTO;

// ...

public List<AutorResponseDTO> buscarAutoresPorNomeContendo(String trecho){
    List<Autor> autores = repository.buscarAutoresPorNomeContendo(trecho);
    return autores.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* A camada de serviço chama o repositório repassando a `String` recebida no parâmetro.
* O retorno do banco é formatado utilizando a função map do Java Stream `.stream().map(mapper::toResponse).toList()`, transformando a entidade em `AutorResponseDTO`. Isso desacopla a resposta da API do formato original armazenado no Banco de Dados.

---

## 3. Camada de Controle/API (`AutorController.java`)

```java
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/buscar")
public List<AutorResponseDTO> buscarAutoresPorNomeContendo(@RequestParam String trecho) {
    try {
        return service.buscarAutoresPorNomeContendo(trecho);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* **`@GetMapping("/buscar")`**: Define a rota GET padrão para buscas dentro do contexto de autores (`/autor/buscar`).
* **`@RequestParam`**: O trecho de texto buscado é passado por parâmetro de URL (Query String, ex: `?trecho=...`). Como é um filtro genérico que pode retornar vários autores diferentes, utilizar `@RequestParam` ao invés de variáveis de rota é a recomendação oficial em arquiteturas REST.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/autor/buscar?trecho=assis`

Se você tiver o autor "Machado de Assis" no banco de dados, esta requisição o retornará com sucesso.

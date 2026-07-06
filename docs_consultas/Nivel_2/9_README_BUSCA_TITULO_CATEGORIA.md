# Implementação: Select Simples em JPQL

Neste documento, iniciamos o **Nível 2**, voltado especificamente para as particularidades e o poder do JPQL (Java Persistence Query Language).

O cenário implementado é: **Selecionar apenas o título dos livros que pertencem a uma determinada categoria**.

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT l.titulo
        FROM Livro l
        WHERE l.categoria = :categoria
        """)
List<String> buscarTitulosPorCategoria(@Param("categoria") String categoria);
```

### Explicação Técnica:
* **`SELECT l.titulo`**: Esta é a grande diferença do Nível 2. Em vez de selecionarmos a Entidade inteira (`SELECT l`), que traria o Livro com ID, preço, datas, autores e editoras conectadas, nós especificamos para o JPQL pegar **apenas a coluna Título**.
* **Retorno `List<String>`**: Como nós estamos projetando (selecionando) apenas o atributo `titulo` que na entidade é do tipo `String`, a nossa Query não retorna mais uma lista de entidades `Livro`. Ela agora devolve um array simples de textos (`List<String>`).
* Isso melhora consideravelmente o tráfego de dados no Banco se você precisar apenas de uma informação específica, pois evita o processamento de entidades pesadas.

---

## 2. Camada de Serviço (`LivroService.java`)

```java
import java.util.List;
// ...

public List<String> buscarTitulosPorCategoria(String categoria){
    return repository.buscarTitulosPorCategoria(categoria);
}
```

### Explicação Técnica:
* A camada de serviço fica extremamente leve. Como não estamos mais lidando com o objeto `Livro` (e sim apenas um pedaço de texto `String`), nós não precisamos fazer conversão para DTOs. Simplesmente repassamos o resultado do Repositório para o Controlador.

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/titulos-por-categoria")
public List<String> buscarTitulosPorCategoria(@RequestParam String categoria) {
    try {
        return service.buscarTitulosPorCategoria(categoria);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* O retorno da API será um array JSON nativo. O método recebe a `categoria` via *Query Parameter*.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/titulos-por-categoria?categoria=Terror`

**Resposta Esperada:**
O Spring retornará apenas uma lista simples de textos.
```json
[
  "It: A Coisa",
  "O Iluminado",
  "O Exorcista"
]
```

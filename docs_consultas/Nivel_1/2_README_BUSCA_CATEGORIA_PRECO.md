# Implementação: Busca de Livros por Categoria e Preço Menor Que

Este documento explica passo a passo a implementação da funcionalidade de busca de livros utilizando dois filtros simultâneos: **Categoria** (igual a um valor) e **Preço** (menor que um valor informado).

## 1. Camada de Repositório (`LivroRepository.java`)

O primeiro passo foi criar a consulta ao banco de dados. No Spring Data JPA, podemos utilizar a anotação `@Query` para escrever a instrução JPQL (Java Persistence Query Language).

```java
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT l
        FROM Livro l
        WHERE l.categoria = :categoria AND l.preco < :preco
        """)
List<Livro> buscarPorCategoriaEPrecoMenorQue(
        @Param("categoria") String categoria, 
        @Param("preco") BigDecimal preco
);
```

### Explicação Técnica:
* **`@Query`**: Permite definir uma consulta customizada usando a sintaxe do JPA (JPQL) que interage com as entidades (Classes Java) e não com as tabelas do banco diretamente.
* **`SELECT l FROM Livro l`**: Seleciona todos os objetos da entidade `Livro`.
* **`WHERE l.categoria = :categoria`**: Filtra os registros onde o atributo `categoria` do livro seja exatamente igual ao parâmetro fornecido.
* **`AND l.preco < :preco`**: Adiciona uma segunda condição obrigatória, exigindo que o `preco` seja estritamente menor (`<`) que o valor fornecido.
* **`@Param(...)`**: Faz o vínculo ("bind") entre os nomes usados na query (`:categoria`, `:preco`) e as variáveis Java recebidas no método.

---

## 2. Camada de Serviço (`LivroService.java`)

A camada de serviço atua como intermediária, contendo as regras de negócio e mapeando o retorno do banco de dados (Entidade) para os objetos de transferência de dados (DTOs).

```java
import java.math.BigDecimal;

// ...

public List<LivroResponseDTO> buscarPorCategoriaEPrecoMenorQue(String categoria, BigDecimal preco){
    List<Livro> livros = repository.buscarPorCategoriaEPrecoMenorQue(categoria, preco);
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* O método recebe os dois parâmetros enviados pelo controlador e aciona a função de busca que criamos no `LivroRepository`.
* A resposta do repositório é uma `List<Livro>`. Porém, não é boa prática expor e devolver a entidade de banco de dados diretamente pela API.
* Utilizamos a API de Streams do Java 8+ (`.stream().map(mapper::toResponse).toList()`) para converter cada entidade `Livro` em um `LivroResponseDTO`, formatando a resposta para o formato final desejado pela API.

---

## 3. Camada de Controle/API (`LivroController.java`)

Por fim, expomos a funcionalidade para o mundo externo criando um "endpoint" HTTP (uma rota da API) que vai receber a requisição e devolver o JSON de resposta ao cliente.

```java
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/buscar")
public List<LivroResponseDTO> buscarPorCategoriaEPreco(
        @RequestParam String categoria, 
        @RequestParam BigDecimal preco){
    try {
        return service.buscarPorCategoriaEPrecoMenorQue(categoria, preco);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* **`@GetMapping("/buscar")`**: Define que esta rota responderá a requisições do tipo `GET` no caminho (URL) `/livro/buscar`.
* **`@RequestParam`**: Extrai os valores passados pela URL. Quando usamos parâmetros de busca (*Query Parameters*) na URL (ex: `?categoria=Aventura&preco=50`), o Spring injeta esses valores diretamente nas variáveis `categoria` e `preco` deste método.
* **Tratamento de Exceções**: A chamada ao serviço é envolvida em um bloco `try-catch` básico. Caso alguma exceção aconteça na camada de serviço (ou de banco), a API intercepta e lança uma `RuntimeException` contendo a mensagem do erro.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/buscar?categoria=Ficcao&preco=50`

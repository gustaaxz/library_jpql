# Implementação: Função de Agregação (AVG)

Continuando nossa exploração no **Nível 2**, este documento mostra como utilizar funções matemáticas embutidas no JPQL (conhecidas como funções de agregação).

O cenário implementado é: **Calcular a média de preço de todos os livros de uma determinada editora.**

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT AVG(l.preco)
        FROM Livro l
        WHERE l.editora.nome = :nomeEditora
        """)
Double calcularMediaPrecoPorEditora(@Param("nomeEditora") String nomeEditora);
```

### Explicação Técnica:
* **`AVG(l.preco)`**: Em vez de retornar um `Livro` ou uma lista de livros, o JPQL utiliza a função `AVG()` (Average/Média) na coluna de preço e pede para o Banco de Dados somar tudo e dividir pela quantidade.
* O Banco de Dados fará toda a matemática complexa e te retornará apenas um único número, o que é infinitamente mais rápido do que buscar 1000 livros para o Java e fazer um laço de repetição (`for`) somando os valores na mão.
* **Retorno `Double`**: Como o retorno é um número fracionado que representa a média, a assinatura do método devolve um tipo compatível (como `Double`).

---

## 2. Camada de Serviço (`LivroService.java`)

```java
public Double calcularMediaPrecoPorEditora(String nomeEditora) {
    return repository.calcularMediaPrecoPorEditora(nomeEditora);
}
```

### Explicação Técnica:
* Repassamos o valor numérico que já vem pronto e mastigado pelo Repository. Nenhuma conversão de DTO é necessária, afinal, é apenas um número bruto.

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/media-preco-por-editora")
// Exemplo de endereçamento: GET http://localhost:8080/livro/media-preco-por-editora?editora=Rocco
public Double calcularMediaPrecoPorEditora(@RequestParam String editora) {
    try {
        return service.calcularMediaPrecoPorEditora(editora);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* A API devolverá como corpo da resposta apenas o valor numérico puro.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/media-preco-por-editora?editora=Rocco`

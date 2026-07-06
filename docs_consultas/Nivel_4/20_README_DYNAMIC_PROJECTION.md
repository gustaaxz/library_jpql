# Implementação: Dynamic Projections (Projeções Dinâmicas)

Chegamos à nossa **20ª e última funcionalidade**! Aqui exploraremos o ápice da reutilização de código no Spring Data JPA.

O cenário implementado é: **Crie um método genérico no repository que aceite um parâmetro genérico <T> para retornar diferentes tipos de Projections para a mesma consulta.**

## 1. Múltiplas Interfaces

Neste ponto nós temos a entidade oficial `Livro.class` e duas interfaces projections:
* `LivroMinimoProjection.class` (Título e Preço)
* `LivroTituloProjection.class` (Apenas Título) - Criada especificamente para este teste.

## 2. Camada de Repositório (`LivroRepository.java`)

```java
<T> List<T> findByCategoria(String categoria, Class<T> type);
```

### Explicação Técnica:
* Ao utilizar Derived Queries (onde o Spring cria a consulta a partir do nome do método, no caso `findByCategoria`), nós podemos adicionar um parâmetro especial `Class<T> type`.
* O Spring é inteligente o suficiente para olhar qual interface ou classe você passou nesse argumento no momento em que você chamou a função, e construirá a consulta SQL no banco de dados customizada sob medida para você, sem que você precise escrever 3 queries diferentes.
* 1 Único Método = Infinitas projeções!

---

## 3. Camada de Serviço e Controller

No Controller, criamos uma lógica bem legal para demonstrar isso:

```java
@GetMapping("/projecao-dinamica")
public ResponseEntity<?> buscarLivrosComProjecaoDinamica(@RequestParam String categoria, @RequestParam String tipo) {
    if ("minima".equalsIgnoreCase(tipo)) {
        return ResponseEntity.ok(service.buscarLivrosPorCategoriaComProjecaoDinamica(categoria, LivroMinimoProjection.class));
    } else if ("titulo".equalsIgnoreCase(tipo)) {
        return ResponseEntity.ok(service.buscarLivrosPorCategoriaComProjecaoDinamica(categoria, LivroTituloProjection.class));
    } else {
        return ResponseEntity.ok(service.buscarLivrosPorCategoriaComProjecaoDinamica(categoria, Livro.class));
    }
}
```

O endpoint escolhe dinamicamente qual formato pedir pro repositório dependendo do parâmetro `tipo` enviado na URL. O Repositório ajustará o SQL que roda no banco de acordo!

## Exemplo de Requisição (Postman/Insomnia)

Teste as 3 variações da mesma Query:
1. `GET http://localhost:8080/livro/projecao-dinamica?categoria=Ficcao&tipo=minima`
2. `GET http://localhost:8080/livro/projecao-dinamica?categoria=Ficcao&tipo=titulo`
3. `GET http://localhost:8080/livro/projecao-dinamica?categoria=Ficcao&tipo=qualquer_outra_coisa`

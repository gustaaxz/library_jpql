# Implementação: Class-based DTO (SELECT new)

Ainda no **Nível 4**, vamos ver a abordagem clássica de Projections: utilizar o construtor de uma classe DTO diretamente dentro da query JPQL.

O cenário implementado é: **Crie um DTO EstatisticasEditoraDTO e use SELECT new ... em JPQL para retornar o nome da editora e a contagem de livros dela.**

## 1. Criação do DTO (`EstatisticasEditoraDTO.java`)

```java
package com.weg.library.dto.editora;

public record EstatisticasEditoraDTO(
    String nomeEditora,
    Long totalLivros
) {}
```
**O que é isso?**
Diferente do exercício anterior que usava uma interface, aqui nós temos um `record` Java normal.

## 2. Camada de Repositório (`LivroRepository.java`)

```java
@Query("""
        SELECT new com.weg.library.dto.editora.EstatisticasEditoraDTO(e.nome, COUNT(l))
        FROM Editora e
        LEFT JOIN e.livros l
        GROUP BY e.nome
        """)
List<EstatisticasEditoraDTO> buscarEstatisticasEditora();
```

### Explicação Técnica:
* **`SELECT new pacote.caminho.completo.DTO(campo1, campo2)`**: A palavra reservada `new` dentro do JPQL indica ao Hibernate que, para cada linha resultante do banco de dados, ele deve chamar o construtor da classe Java informada (passando o pacote completo). 
* É obrigatório que exista um construtor na classe (ou Record) cujos tipos correspondam exatamente aos tipos que a Query está devolvendo (uma `String` pro nome e um `Long` que é o padrão do `COUNT`).
* Assim como nas Interfaces Projection, o grande ganho de desempenho aqui é que o Hibernate não perde tempo construindo a pesada entidade Editora e os Livros, ele apenas joga os dados literais direto para dentro do seu DTO leve.

---

## 3. Camada de Serviço e Controller

* **Endpoint:** `GET /livro/estatisticas-editora`

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/estatisticas-editora`

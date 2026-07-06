# Implementação: Interface Projections

Com esta implementação iniciamos o **Nível 4**, voltado totalmente para otimização de consultas e seleção seletiva de campos (Projections e DTOs focados).

O cenário implementado é: **Crie uma interface LivroMinimoProjection (apenas titulo e preco) e um método no repository que a utilize.**

## 1. Criação da Interface Projection (`LivroMinimoProjection.java`)

```java
package com.weg.library.projection;
import java.math.BigDecimal;

public interface LivroMinimoProjection {
    String getTitulo();
    BigDecimal getPreco();
}
```
**O que é isso?**
O Spring Data JPA nos permite criar uma interface apenas com os métodos "Getters" das colunas que nos interessam. O Spring automaticamente criará uma classe por baixo dos panos na hora de rodar a query para preencher esses dados. Isso é mais leve do que trazer a Entidade inteira com todos os seus relacionamentos.

## 2. Camada de Repositório (`LivroRepository.java`)

```java
@Query("""
        SELECT l.titulo AS titulo, l.preco AS preco
        FROM Livro l
        """)
List<LivroMinimoProjection> buscarLivrosComProjecao();
```

### Explicação Técnica:
* **`SELECT l.titulo AS titulo, l.preco AS preco`**: Selecionamos especificamente os campos no JPQL e utilizamos a palavra `AS` (Alias) para garantir que o nome da coluna no resultado seja exatamente idêntico ao getter da nossa interface (`getTitulo` = `AS titulo`).
* **Retorno `List<LivroMinimoProjection>`**: O Hibernate faz a mágica de preencher nossa interface com as tuplas vindas do banco de dados, ignorando completamente campos pesados como listas de autores, relacionamentos e datas.

---

## 3. Camada de Serviço e Controller

* **Endpoint:** `GET /livro/projecao-minima`
* Note que na camada de serviço **não foi necessário converter** para DTO com o Mapper. A interface Projection por si só já é segura o suficiente para ser retornada direto pelo Controller, visto que ela já isola os dados pesados e sensíveis da Entidade real.

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/projecao-minima`

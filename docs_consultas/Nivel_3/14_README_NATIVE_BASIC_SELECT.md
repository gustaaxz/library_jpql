# Implementação: Native Query (SQL Puro) - Select Básico

Iniciamos o **Nível 3**, onde largamos o poder interpretativo do JPQL e escrevemos Queries diretamente na linguagem oficial do banco de dados (SQL Puro). 

O cenário implementado é: **Faça uma query nativa para selecionar todos os campos da tabela livro onde a data_publicacao seja no ano de 2023.**

## 1. Camada de Repositório (`LivroRepository.java`)

```java
@Query(value = """
        SELECT *
        FROM livro
        WHERE YEAR(data_publicacao) = 2023
        """, nativeQuery = true)
List<Livro> buscarLivrosPorAno2023();
```

### Explicação Técnica:
* **`nativeQuery = true`**: É isso que transforma a consulta de JPQL para SQL nativo. O Hibernate não tentará traduzir ou validar sua consulta contra suas Entidades Java; ele vai repassar esse texto cegamente direto para o Banco de Dados.
* **`FROM livro`**: Perceba que agora estamos acessando `livro` (minúsculo, nome real da tabela no banco) e não `Livro` (maiúsculo, nome da classe Java).
* **`YEAR()`**: Uma função nativa exclusiva de bancos MySQL (e SQL Server) que extrai o ano de um campo de data.

---

## 2. Camada de Serviço e Controller

* **Endpoint:** `GET /livro/ano-2023`

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/ano-2023`

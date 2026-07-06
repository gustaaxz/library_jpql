# Implementação: Native Projections (SQL Puro para Interface)

No **Nível 4**, vimos que Projections em JPQL são muito eficientes. Mas e se a nossa consulta for tão complexa (ou usar recursos tão específicos do banco) que formos obrigados a usar SQL Nativo? Dá pra projetar o resultado nativo diretamente para uma Interface? Sim!

O cenário implementado é: **Utilize uma Query Nativa para mapear o resultado para uma Interface Projection.**

## 1. A Interface Projection

Reutilizamos a nossa interface do passo 17, `LivroMinimoProjection`, que possui os getters `getTitulo()` e `getPreco()`.

## 2. Camada de Repositório (`LivroRepository.java`)

```java
@Query(value = """
        SELECT titulo AS titulo, preco AS preco
        FROM livro
        """, nativeQuery = true)
List<LivroMinimoProjection> buscarLivrosComProjecaoNativa();
```

### Explicação Técnica:
* **`nativeQuery = true`**: Escrevemos a consulta em SQL puro acessando a tabela `livro` (minúsculo).
* **`AS titulo` e `AS preco`**: Em consultas nativas mapeadas para Interfaces Projection, o uso dos aliases (`AS`) não é apenas uma boa prática, mas é **quase obrigatório** em alguns bancos de dados para garantir que o framework de mapeamento do Spring JPA consiga relacionar a coluna de retorno bruta do SQL com o método `getTitulo()` da interface Java. A coincidência exata de nomes via Alias é o que faz a mágica acontecer aqui.

---

## 3. Camada de Serviço e Controller

* **Endpoint:** `GET /livro/projecao-nativa`
* Novamente, não precisamos de Mappers. O serviço apenas recebe a interface preenchida do repositório e repassa ao controlador.

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/projecao-nativa`

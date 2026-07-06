# Implementação: Native Query - Funções do Banco de Dados

Finalizando o **Nível 3**, vamos testar algumas funções literais do banco.

O cenário implementado é: **Utilizar a função LOWER do SQL para buscar livros por categoria, ignorando maiúsculas e minúsculas via Native Query.**

## 1. Camada de Repositório (`LivroRepository.java`)

```java
@Query(value = """
        SELECT *
        FROM livro
        WHERE LOWER(categoria) = LOWER(:categoria)
        """, nativeQuery = true)
List<Livro> buscarLivrosPorCategoriaIgnorandoCaixaNative(@Param("categoria") String categoria);
```

### Explicação Técnica:
* **`LOWER()`**: É uma função padrão SQL que transforma o texto em letras minúsculas. 
* Ao colocar `LOWER` tanto na coluna do banco quanto no parâmetro vindo do usuário, nós forçamos uma comparação onde "Terror", "TERROR" e "teRRor" viram, na hora da checagem, a mesma palavra: "terror". Dessa forma o filtro acha o livro independente de como o usuário digitou.
* É importante notar que rodar funções em cima de colunas (como `LOWER(categoria)`) pode desativar os índices do banco de dados para aquela coluna, causando lentidão em tabelas de milhões de registros.

---

## 2. Camada de Serviço e Controller

* **Endpoint:** `GET /livro/categoria-ignorando-caixa`

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/categoria-ignorando-caixa?categoria=fIcCaO`

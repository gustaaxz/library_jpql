# Implementação: Contagem de Livros (Count) com Relacionamentos (Join)

Este documento demonstra como implementar uma função de contagem (`COUNT`), agregando os dados sem precisar recuperar e trafegar todas as entidades do banco de dados para a API, e como fazer a junção explícita (`JOIN`) com outra tabela (neste caso, a relação Muitos-Para-Muitos entre `Livro` e `Autor`).

O cenário implementado é: **Contar quantos livros existem na biblioteca cuja nacionalidade do autor seja uma específica**.

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT COUNT(DISTINCT l)
        FROM Livro l
        JOIN l.autores a
        WHERE a.nacionalidade = :nacionalidade
        """)
Long contarLivrosPorNacionalidadeDeAutor(@Param("nacionalidade") String nacionalidade);
```

### Explicação Técnica:
* **`COUNT(DISTINCT l)`**: Em vez de retornar objetos do tipo `Livro`, a função `COUNT` faz com que o banco apenas some a quantidade de registros encontrados e nos devolva um número. O `DISTINCT` garante que, se um mesmo livro tiver dois autores brasileiros, o livro será contado apenas uma vez.
* **Retorno `Long`**: Como estamos usando `COUNT`, a assinatura do método muda. Não retornamos mais uma `List<Livro>`, mas sim um `Long` (tipo primitivo empacotado para números inteiros grandes), que é o padrão de retorno do JPA para contagens.
* **`JOIN l.autores a`**: A entidade `Livro` possui uma lista de autores chamada `autores`. Como nacionalidade é um atributo do Autor (e não do Livro), precisamos "juntar" (*JOIN*) as informações do Livro com a do Autor. Chamamos esse elo de "a" (o *alias*).
* **`WHERE a.nacionalidade = :nacionalidade`**: Agora, filtramos a consulta verificando se a nacionalidade do autor ("a") bate com o parâmetro.

---

## 2. Camada de Serviço (`LivroService.java`)

```java
public Long contarLivrosPorNacionalidadeDeAutor(String nacionalidade){
    return repository.contarLivrosPorNacionalidadeDeAutor(nacionalidade);
}
```

### Explicação Técnica:
* O método não precisa de mapeamentos (`.stream().map()`) ou de DTOs de Resposta (como o `LivroResponseDTO`). Isso porque um número isolado (`Long`) já é um tipo de dado limpo que não expõe atributos sensíveis de uma Entidade de banco de dados. Podemos simplesmente repassar o número diretamente da Repository para o Controller.

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/contar-por-nacionalidade-autor")
public Long contarLivrosPorNacionalidadeDeAutor(@RequestParam String nacionalidade) {
    try {
        return service.contarLivrosPorNacionalidadeDeAutor(nacionalidade);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* O retorno do Endpoint mudou de `List<LivroResponseDTO>` para apenas `Long`. O Spring cuidará para que a API retorne este número no corpo (Body) da resposta HTTP como texto plano ou formato numérico simples.
* **`@RequestParam`**: A variável de busca (`nacionalidade`) continua sendo passada via Query String, acompanhando o padrão REST.

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/contar-por-nacionalidade-autor?nacionalidade=Brasileiro`

O banco rodará um SQL otimizado apenas para agregar (`COUNT`), o que é absurdamente mais rápido que recuperar milhares de dados e depois ver o `tamanho` de um Array via Javascript/Java. A API responderá de volta com algo como `15`, sem chaves `{}` JSON.

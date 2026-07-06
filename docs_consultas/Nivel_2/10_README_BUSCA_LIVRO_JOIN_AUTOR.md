# Implementação: Uso Básico de JOIN no JPQL

Continuando no **Nível 2**, este documento demonstra como utilizar o comando `JOIN` em JPQL para filtrar registros de uma entidade baseados em condições de uma entidade relacionada (Muitos-para-Muitos ou Um-para-Muitos).

O cenário implementado é: **Selecionar todos os livros onde o nome do Autor seja igual a um parâmetro passado**.

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT l
        FROM Livro l
        JOIN l.autores a
        WHERE a.nome = :nomeAutor
        """)
List<Livro> buscarLivrosPorNomeAutor(@Param("nomeAutor") String nomeAutor);
```

### Explicação Técnica:
* **`JOIN l.autores a`**: Nós vimos na documentação de contagem (Query 8) o uso do JOIN, mas é importante reforçá-lo como o padrão primário para transição entre Entidades no JPA. Diferente do SQL puro onde escrevemos `JOIN autor ON autor.id = livro_autores.id_autor`, no JPQL a gente apenas navega pela lista mapeada (`l.autores`) e damos um apelido (`a`). O Hibernate monta toda a estrutura complexa do banco (tabelas intermediárias) automaticamente para você.
* **`WHERE a.nome`**: Uma vez que criamos a ponte (`JOIN`) e demos o apelido `a`, agora podemos acessar e comparar as colunas pertencentes unicamente ao Autor.

---

## 2. Camada de Serviço (`LivroService.java`)

```java
import java.util.List;
// ...

public List<LivroResponseDTO> buscarLivrosPorNomeAutor(String nomeAutor){
    List<Livro> livros = repository.buscarLivrosPorNomeAutor(nomeAutor);
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* Nada de novo aqui. A query do repositório já devolve a lista filtrada corretamente, então o serviço apenas converte os objetos de Entidade para DTOs a fim de enviá-los para a API com segurança.

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/buscar-por-autor")
public List<LivroResponseDTO> buscarLivrosPorNomeAutor(@RequestParam String nomeAutor) {
    try {
        return service.buscarLivrosPorNomeAutor(nomeAutor);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* O endpoint de GET recebe via *Query Parameter* (`@RequestParam`) o nome do autor a ser pesquisado. 

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL:** `http://localhost:8080/livro/buscar-por-autor?nomeAutor=J.R.R. Tolkien`

O Spring Data fará o join no banco, encontrará o autor com o nome exato passado e retornará todos os livros associados a ele, já formatados em JSON pela DTO.

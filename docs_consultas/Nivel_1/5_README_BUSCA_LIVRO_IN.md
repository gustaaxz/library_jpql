# Implementação: Busca de Livros por Múltiplas Categorias (IN)

Este documento explica a implementação da funcionalidade de busca de livros cujo atributo "categoria" pertença a uma lista de valores predefinidos informados pelo usuário, como, por exemplo, buscar todos os livros que sejam de 'Terror' OU 'Ficção'.

## 1. Camada de Repositório (`LivroRepository.java`)

```java
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ...

@Query("""
        SELECT l
        FROM Livro l
        WHERE l.categoria IN :categorias
        """)
List<Livro> buscarPorCategorias(@Param("categorias") List<String> categorias);
```

### Explicação Técnica:
* **`IN`**: O operador `IN` no SQL/JPQL serve como uma versão simplificada de múltiplos `OR`. Ao invés de escrever `l.categoria = 'Ficcao' OR l.categoria = 'Terror'`, utilizamos `IN` e passamos uma lista.
* **`List<String>`**: O JPA é inteligente e consegue receber uma coleção/lista (`List`) do Java e transcrever automaticamente para a notação de lista do SQL `('Ficcao', 'Terror')`.

---

## 2. Camada de Serviço (`LivroService.java`)

```java
import java.util.List;
// ...

public List<LivroResponseDTO> buscarPorCategorias(List<String> categorias){
    List<Livro> livros = repository.buscarPorCategorias(categorias);
    return livros.stream()
                .map(mapper::toResponse)
                .toList();
}
```

### Explicação Técnica:
* A camada de serviço simplesmente repassa a lista de strings recebida no parâmetro diretamente para o repositório.
* Após retornar do banco, fazemos a conversão padrão dos objetos `Livro` recuperados pelo Hibernate para o objeto de transferência que vai para a API (`LivroResponseDTO`).

---

## 3. Camada de Controle/API (`LivroController.java`)

```java
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ...

@GetMapping("/buscar-por-categorias")
public List<LivroResponseDTO> buscarPorCategorias(@RequestParam List<String> categorias) {
    try {
        return service.buscarPorCategorias(categorias);
    } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### Explicação Técnica:
* **`@GetMapping("/buscar-por-categorias")`**: Definimos um novo *endpoint* HTTP do tipo GET.
* **`@RequestParam List<String> categorias`**: O Spring consegue ler listas vindas através dos *Query Parameters* da URL automaticamente de duas formas. Ele pode separar por vírgula (`categorias=Terror,Ficcao`) ou através da repetição do parâmetro na URL (`categorias=Terror&categorias=Ficcao`).

---

## Exemplo de Requisição (Postman/Insomnia)

**Método:** `GET`  
**URL (Padrão 1):** `http://localhost:8080/livro/buscar-por-categorias?categorias=Ficcao,Terror,Romance`

**URL (Padrão 2):** `http://localhost:8080/livro/buscar-por-categorias?categorias=Ficcao&categorias=Terror`

Qualquer um dos dois padrões acima fará o Spring preencher a variável `List<String> categorias` corretamente com os itens informados.

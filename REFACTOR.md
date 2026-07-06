# 🛠️ Documentação de Refatoração e Bugs (REFACTOR.md)

Este documento registra as refatorações estruturais e correções de bugs feitas no projeto, além de explicar o contexto de cada mudança para fins de aprendizado (especialmente particularidades da JPA e do ecossistema Spring).

---

## 1. Correção do Erro `TransientPropertyValueException` (Efeito Cascata)

**Contexto do Erro:**
Ao tentar cadastrar um novo `Livro` informando no JSON os dados de uma nova `Editora` (que ainda não possuía um `id` no banco), o Hibernate lançava a exceção `TransientPropertyValueException`. O motivo era que a entidade `Livro` estava tentando se associar a um objeto que estava em estado *Transient* (não salvo), e o JPA bloqueava a ação por segurança.

**Mudança Feita (`Livro.java`):**
Foi adicionada a propriedade `cascade` na anotação `@ManyToOne` dentro da entidade Livro.

```diff
-    @ManyToOne
+    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
     @JoinColumn(name = "id_editora")
     private Editora editora;
```

**Resultado:**
O JPA passa a entender que deve salvar primeiramente a nova `Editora` no banco de dados, gerar o ID dela, e logo em seguida vincular este novo ID ao `Livro` que está sendo cadastrado em uma única transação.

---

## 2. Sincronização de Relacionamento Bidirecional em Memória

**Contexto do Erro:**
Após resolver o problema da cascata, o livro e a editora eram salvos perfeitamente no banco de dados. Contudo, a resposta JSON imediata do `POST /livro` retornava a editora com a sua propriedade `livros` vazia/nula. Isso ocorria porque o JPA não sincroniza relacionamentos bidirecionais automaticamente na memória do Java até que a transação feche e uma nova consulta seja feita ao banco.

**Mudança Feita (`LivroMapper.java`):**
Ajustamos a classe responsável pela conversão para forçar a sincronização de ambos os lados da moeda em memória antes de devolver a resposta.

```diff
    public Livro toEntity(LivroRequestDTO requestDTO){
        Livro livro = new Livro(...);

+        // Garante que o livro recém-criado seja colocado na lista da editora correspondente
+        if (livro.getEditora() != null) {
+            livro.getEditora().getLivros().add(livro);
+        }
        
        return livro;
    }
```

---

## 3. Prevenção de Loop Infinito na Serialização JSON (StackOverflowError)

**Contexto do Erro:**
Ao resolver o passo 2 (colocar o Livro dentro da lista da Editora), criamos um efeito colateral fatal durante a serialização da resposta pelo Jackson (a biblioteca que transforma os Objetos Java em texto JSON): 
O Jackson lia o Livro -> Entrava na Editora -> Entrava na Lista de Livros -> Entrava na Editora -> Entrava na Lista de Livros... gerando um Loop Infinito que derrubava a aplicação com `StackOverflowError`.

**Mudança Feita (`Editora.java`):**
Inicializamos a lista vazia (`new ArrayList<>()`) para evitar NullPointers e aplicamos a anotação `@JsonIgnoreProperties` para "cortar" o ciclo.

```diff
+import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
+import java.util.ArrayList;

// ...

    @OneToMany(mappedBy = "editora")
+   @JsonIgnoreProperties("editora")
-   private List<Livro> livros;
+   private List<Livro> livros = new ArrayList<>();
```

**Resultado:**
Agora, quando o Jackson for desenhar a lista de livros que pertencem a uma Editora, ele vai propositalmente ocultar a propriedade `editora` de dentro desses livros, quebrando o espelho infinito de repetições.

---

## 4. Inserção de Autores via JSON na Criação de Livros

**Contexto da Mudança:**
Anteriormente, não era possível associar um ou mais `Autor` ao criar um `Livro` via POST, pois o objeto `LivroRequestDTO` não possuía nenhum campo para receber essa informação, e o `LivroMapper` não os processava.

**Mudança Feita (`LivroRequestDTO.java` e `LivroMapper.java`):**
Adicionamos a lista de autores no DTO de entrada e ajustamos o Mapper para repassar essa lista para a Entidade de domínio.

```diff
// LivroRequestDTO.java
public record LivroRequestDTO (
    // ...
    String categoria,
    Editora editora,
+   List<Autor> autores
){
```

```diff
// LivroMapper.java
        Livro livro = new Livro(
            // ...
        );
        
+       livro.setAutores(requestDTO.autores());
```

**Resultado:**
Agora, na rota `POST /livro`, é possível associar autores pré-existentes a um novo livro apenas passando o JSON contendo a lista de seus IDs (ex: `"autores": [{"id": 1}]`). O JPA entende o vínculo da relação ManyToMany automaticamente.

---

## 5. Resolução de Entidades com Campos Nulos na Resposta JSON (Efeito Stub)

**Contexto do Erro:**
Após permitir o envio de Autores pelo JSON apenas usando seus IDs (ex: `{"id": 1}`), o livro era salvo corretamente, mas a resposta da API (Response) devolvia o autor com todas as suas outras propriedades (`nome`, `nacionalidade`, `dataNascimento`) como `null`. Isso ocorria porque o framework Jackson cria um objeto "stub" (fantasma) preenchendo apenas o ID fornecido e o Hibernate não refaz a consulta automaticamente após o `save()`. O objeto fantasma acabava sendo devolvido diretamente ao usuário.

**Mudança Feita (`LivroService.java`):**
Injetamos o `AutorRepository` dentro do serviço de Livros. Antes de salvar o livro, extraímos os IDs fornecidos no JSON e realizamos uma busca real no banco de dados para substituir os "fantasmas" pelos objetos originais e completos.

```diff
    public LivroResponseDTO postLivro(LivroRequestDTO requestDTO){
        Livro livro = mapper.toEntity(requestDTO);
        
+       // Busca os autores completos no banco para não retornar os nulos do JSON
+       if (livro.getAutores() != null && !livro.getAutores().isEmpty()) {
+           List<Long> autorIds = livro.getAutores().stream().map(a -> a.getId()).toList();
+           livro.setAutores(autorRepository.findAllById(autorIds));
+       }

        livro = repository.save(livro);
        return mapper.toResponse(livro);
    }
```

**Resultado:**
A resposta JSON do POST agora traz o Livro recém-criado contendo a listagem com todos os dados preenchidos dos autores associados, tornando o retorno perfeitamente previsível para o front-end.

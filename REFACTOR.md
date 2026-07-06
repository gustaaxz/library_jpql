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
*(Nota: O mesmo erro aconteceu mais tarde com a relação ManyToMany entre `Livro` e `Autor`, resultando no erro "Document nesting depth (501) exceeds the maximum allowed").*

**Mudança Feita (`Editora.java` e `Autor.java`):**
Inicializamos as listas vazias (`new ArrayList<>()`) para evitar NullPointers e aplicamos a anotação `@JsonIgnoreProperties` para "cortar" o ciclo de ambos os lados.

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

---

## 6. Criação Dinâmica de Autores via Cascade (Junto com o Livro)

**Contexto da Mudança:**
Anteriormente, o sistema exigia que os autores já existissem no banco de dados, sendo possível associá-los ao livro apenas fornecendo seus IDs. O objetivo era dar mais flexibilidade à API, permitindo que o usuário envie os dados de um autor que ainda não existe (sem ID) e o sistema se encarregue de criá-lo e vinculá-lo ao livro de uma só vez.

**Mudança Feita (`Livro.java` e `LivroService.java`):**
Adicionamos o comportamento de cascata `CascadeType.PERSIST` na lista de autores do `Livro.java`. Depois, alteramos a lógica de resgate no `LivroService.java` para ser inteligente: se o objeto de Autor vier com um ID no JSON, ele faz a busca no banco; se vier sem ID (nulo), ele entende que é um autor novo, não faz a busca, e deixa o JPA cuidar de persistir ele no banco por cascata.

```diff
// Livro.java
-    @ManyToMany
+    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
     @JoinTable(...)
     private List<Autor> autores;
```

```diff
// LivroService.java
+        if (livro.getAutores() != null && !livro.getAutores().isEmpty()) {
+            List<Autor> autoresFinais = new java.util.ArrayList<>();
+            for (Autor autor : livro.getAutores()) {
+                if (autor.getId() != null) {
+                    autoresFinais.add(autorRepository.findById(autor.getId()).orElse(autor));
+                } else {
+                    autoresFinais.add(autor);
+                }
+            }
+            livro.setAutores(autoresFinais);
+        }
```

**Resultado:**
Ao fazer um `POST /livro`, agora é possível enviar arrays de autores mistos. O JPA vinculará ao livro os autores que possuírem `"id"`, enquanto criará novos registros na tabela de Autores para aqueles que enviarem apenas `"nome"` e `"nacionalidade"`.

---

## 7. Exibição de Listas Relacionadas no Objeto de Resposta (ResponseDTO)

**Contexto do Erro:**
Mesmo após tratar a criação em cascata e o efeito "Stub" dos autores, os dados não estavam sendo exibidos no JSON final retornado pela API. Isso acontece porque no padrão de arquitetura DTO (Data Transfer Object), as requisições (Request) e as respostas (Response) são contratos rígidos separados. O `LivroResponseDTO` não possuía a propriedade `autores`.

**Mudança Feita (`LivroResponseDTO.java` e `LivroMapper.java`):**
Adicionamos a lista de autores no contrato de resposta e no Mapper.

```diff
// LivroResponseDTO.java
public record LivroResponseDTO (
    // ...
    String categoria,
    Editora editora,
+   List<Autor> autores
){
```

```diff
// LivroMapper.java
        return new LivroResponseDTO(
            // ...
            livro.getCategoria(),
            livro.getEditora(),
+           livro.getAutores()
        );
```

**Resultado:**
A API passa a desenhar perfeitamente o array de `autores` na resposta visual do Postman/Frontend logo após a criação ou listagem do Livro.

---

## 8. Limpeza do JSON de Resposta (Ocultando 'livros' Aninhados)

**Contexto da Mudança:**
Ao buscar um livro ou logo após criá-lo, o JSON retornado trazia as informações completas da `Editora` e dos `Autores` vinculados. Contudo, como esses objetos possuíam suas próprias listas de livros, o JSON ficava poluído devolvendo os dados do próprio livro em duplicidade dentro das entidades aninhadas (mesmo sem o loop infinito).

**Mudança Feita (`LivroResponseDTO.java` e `LivroMapper.java`):**
Em vez de utilizar anotações como `@JsonIgnore` nas Entidades de banco (o que prejudicaria outras rotas que pudessem precisar dessas informações), passamos a usar os DTOs específicos de Resposta para a Editora e o Autor (`EditoraResponseDTO` e `AutorResponseDTO`). Como esses DTOs não possuem o atributo `livros` em seu contrato, a lista é naturalmente omitida.

```diff
// LivroResponseDTO.java
public record LivroResponseDTO (
    // ...
    String categoria,
-   Editora editora,
+   EditoraResponseDTO editora,
-   List<Autor> autores
+   List<AutorResponseDTO> autores
){
```

```diff
// LivroMapper.java
            livro.getCategoria(),
-           livro.getEditora(),
+           livro.getEditora() != null ? editoraMapper.toResponse(livro.getEditora()) : null,
-           livro.getAutores()
+           livro.getAutores() != null ? livro.getAutores().stream().map(autorMapper::toResponse).collect(Collectors.toList()) : null
        );
```

**Resultado:**
O JSON da rota de Livros agora fica extremamente limpo e objetivo, mostrando os dados da Editora e dos Autores através de seus DTOs limpos, sem "rebater" de volta a listagem vazia de livros que eles possuem nas entidades originais. O uso de DTOs isola as responsabilidades e evita gambiarras com anotações nas Entidades principais.

---

## 9. Prevenção de Duplicação de Entidades no Cascade (Editora)

**Contexto do Erro:**
Mesmo após permitir a criação de um Livro associando-o a uma Editora que já existia (passando `"id": 3` no JSON), o JPA acabava ignorando esse ID e criando uma **nova linha idêntica** no banco de dados (ex: `id = 4`). Isso acontecia porque a chave primária da Editora usa a estratégia `@GeneratedValue(strategy = GenerationType.IDENTITY)`. Quando passamos um objeto solto (detached) com ID manual e forçamos o `CascadeType.PERSIST` do Livro sobre ela, o banco ignora o ID manual e insere um registro novo.

**Mudança Feita (`LivroService.java`):**
Injetamos o `EditoraRepository` no `LivroService`. Agora, antes de chamar o `save()` do Livro, interceptamos o DTO: se a Editora veio com um ID, nós vamos no banco de dados, buscamos a Editora "oficial" que já está lá (managed) e amarramos ela ao Livro. 

```diff
// LivroService.java
        Livro livro = mapper.toEntity(requestDTO);
        
+       // Se a editora vier com ID, busca do banco para evitar duplicar via CascadeType.PERSIST
+       if (livro.getEditora() != null && livro.getEditora().getId() != null) {
+           livro.setEditora(editoraRepository.findById(livro.getEditora().getId()).orElse(livro.getEditora()));
+       }
```

**Resultado:**
O comportamento se divide corretamente. Se a requisição contiver apenas os dados da editora (sem ID), o Hibernate a salvará como uma nova editora. Se contiver o `"id"`, ele buscará a editora existente e a reaproveitará para o novo Livro sem gerar registros duplicados (linhas fantasmas) na tabela.

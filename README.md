﻿# SPRING DATA JPA
Descrição: Este repositório tem como objetivo estudar algumas funcionalidades do Spring Data JPA, entendendo suas notações e funcionalidades que geram facilidades na hora de gerir e controlar bancos de dados. Como guia deste estudo, foi utilizado como base o vídeo <a href="https://www.youtube.com/watch?v=Ca30sv9EbLo&t=2129s&ab_channel=MichelliBrito">Spring Data JPA</a> | Curso 2024 da Michelli Brito

---

## O que é o Spring JPA:
Ele é uma abstração da biblioteca JPA (Java Persistence API) do Java, inserindo algumas funcionalidades que facilitam a implementação de métodos e relacionamentos no seu banco de dados. Neste repostório, iremos estudar algumas notações comumente utilizadas durante o desenvolvimento, são elas:

- @Entity
- @Table
- @Id
- @GeneratedValue
- @Column
- @Transactional
- @ManyToMany
- @ManyToOne
- @OneToMany
- @OneToOne
- @JoinTable
- @JoinColumn

---

## Sobre o projeto
Este projeto consiste num sistema de uma livraria, onde teremos o controle sobre 4 entidades e seus relacionamentos. Segue abaixo o guia de relacionamentos utilizado como base para seu desenvolvimento: 

<img src="src/main/java/com/bookstore/jpa/utils/images/diagramaClasses.png" alt="Diagrama de Classe do Projeto">

---
## Annotations estudadas:

- @Entity: Essa notação diz ao JPA que essa classe deve ser mapeada como uma entidade do Banco de dados, ou seja, ela será armazenada em alguma tabela e nós delegamos a responsaibilidade de mapear a Classe para o JPA.
- @Table: Notação responsável por informação ao JPA a tabela à qual ele deverá mapear a entidade.
- @Id: Define o atributo ao qual está vinculado como chave primária.
- @GeneratedValue: Define a forma como o ID será gerado, se ele vai ser automática, identitária etc.
- @Column: Define algumas propriedades da coluna à qual determinado atributo está relacionado, como:
  - Nome da coluna
  - Se pode ou não ser null
  - Se é único ou não
- @ManyToOne: Indica um tipo de relacionamento onde vários elementos da entidade onde a notação foi chamada podem ser referenciados à um item especifico do atributo em questão. Por exemplo, no BookModel, vários livros podem estar ligados a uma única editora. 
- @OneToMany: Indica um relacionamento onde uma entidade pode conter diversos elementos ligados à ela. Alguma propriedades são importantes de sere definidas, são elas:
  - mappedBy: Define o nome da coluna mapeada dos objetos que estão ligados à ele. Por exemplo, nas editoras, essa propriedade recebe "publisher", pois foi o nome dado a coluna da TB_BOOK que indica o relacionamento de Book com Publisher
  - fetch: Define o momento em que a subConsulta dos dados relacionados será executada, pode ser LAZY (Preguiçoso/depois) ou EAGER (Cedo/antes).
- @JoinColumn: define o nome da coluna que será considerada chave estrangeira na tabela
- @Transactional: Em métodos em que existem uma alta movimentação no banco de dados, por exemplo, quando criar um usuario, ele vai criar uma review e buscar dados de publisher e authors, caso alguns dos passos resulte em erro na hora do salvamento, ele não irá implementar os outros. Ou seja, ele não irá salvar uma review sem que o salvamento de usuário tenha ocorrido com sucesso. 

--- 

## Relacionamentos

### @OneToMany e @ManyToOne
O primeiro relacionamento que iremos abordar é o entre Publisher e Book, da seguinte forma:

<img src="src/main/java/com/bookstore/jpa/utils/images/one-to-many--many-to-one.png" alt="Relacionamento One to Many e Many to One">

A lógica é a seguinte, uma editora pode publicar diversos livros, mas um livro pode ser publicado apenas por uma editora. Nesse contexto, entra o relacionamento do tipo OneToMany e ManyToOne. Segue o código de como foi implementado o relacionamento nas classes de BookModel e PublisherModel:

```
public class BookModel implements Serializable {

  {...}
  
  @ManyToOne
  @JoinColumn(name = "publisher_id")
  private PublisherModel publisher;

  )
  
  {...}
  
}

  
```

```
public class PublisherModel implements Serializable {
   
  {...}
   
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
  private Set<BookModel> books = new HashSet<>();
    
  {...}
}


```


### @ManyToMany
O relacionamento do tipo ManyToMany diz respeito ao tipo de relação onde diversas entidades podem se relacionar com diversas outras, como na imagem exibida abaixo:

<img src="src/main/java/com/bookstore/jpa/utils/images/many-to-many.png" alt="Relacionamento Many to Many">

Para este tipo de relacionamento, o ideal é criar uma tabela auxiliar para receber os dados e ser capaz de linkar os dois. Para isso, escolha uma das classes para estabelecer uma nova tabela:

```
public class BookModel implements Serializable {
    
  {...}

  @ManyToMany//(fetch = FetchType.LAZY)
  @JoinTable(
          name = "tb_book_author",
          joinColumns = @JoinColumn(name = "book_id"),
          inverseJoinColumns = @JoinColumn(name = "author_id")
  )
  private Set<AuthorModel> authors = new HashSet<>();
  
  {...}
}

```

Após essa configuração, você deve ir na outra class, no nosso contexto, na classe de AuthorModel e criar um Hash que irá linkar esses dados via o atributo authors do BookModel:

```
public class AuthorModel implements Serializable {

  {...}
    
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
  private Set<BookModel> books = new HashSet<>();
  
  {...}

}

```

### @OneToOne
Esse relacionamento diz respeito à entidades que possuem um relacionamento um para um, ou seja, um item pode ser relacionado com apenas um outro item do outro objeto.

<img src="src/main/java/com/bookstore/jpa/utils/images/one-to-one.png" alt="Relacionamento One to One">

No nosso contexto, temos que uma review pode ser aplicada apenas para um livro, e um livro pode ter apenas uma review, o que configura o relacionamento one to one. Segue abaixo a implementação do relacionamento nas classes referenciadas:

```
public class BookModel implements Serializable {
    
  {...}

  @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
  private ReviewModel review;
  
  {...}
}

```

```
public class ReviewModel implements Serializable {
  
  {...}
    
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @OneToOne
  @JoinColumn(name = "book_id")
  private BookModel book;
    
  {...}
  
}

```

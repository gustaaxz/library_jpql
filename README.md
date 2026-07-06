# 📚 Library Management API (Focus on JPQL Queries)

This is a RESTful API project for managing a library, developed with **Java 21**, **Spring Boot**, and **Spring Data JPA**, using **MySQL** as the database. 

The main focus of this repository is to demonstrate the **implementation of complex database queries using JPQL** (Java Persistence Query Language).

📄 **System Context Document:** [Contexto do Sistema - Gestão de Biblioteca.pdf](./Contexto%20do%20Sistema_%20Gest%C3%A3o%20de%20Biblioteca.pdf)

---

## 🔎 JPQL Queries Documentation

The learning and implementation of the queries were divided into difficulty levels. Each folder below contains detailed step-by-step explanations of how the query was implemented, from the Database (Repository) to the API exposure (Controller).

Access the explanatory guides:
- 🟢 **[Level 1: Derived Query Methods (Padrão Spring Data)](./docs_consultas/Nivel_1)**
- 🟡 **[Level 2: JPQL (Java Persistence Query Language)](./docs_consultas/Nivel_2)**
- 🟠 **[Level 3: Native Queries (SQL Puro)](./docs_consultas/Nivel_3)**
- 🔴 **[Level 4: Projections e DTOs](./docs_consultas/Nivel_4)**

---

## 🛠️ Refactoring & Bug Fixes

Throughout the development of this API, several structural challenges were overcome (such as infinite recursion with Jackson, Many-to-Many cascade saves, and memory synchronization). 
All of these architectural decisions and bug fixes are detailed in the **[REFACTOR.md](./REFACTOR.md)** file.

---

## 🚀 Technologies Used

- **Java 21**
- **Spring Boot 3**
  - Spring Boot Starter Web
  - Spring Data JPA
  - Spring Boot DevTools
- **MySQL** (Driver: `mysql-connector-j`)
- **Lombok** (For boilerplate code reduction)
- **Maven** (Dependency manager)

---

## 🛠️ Environment Setup

### 1. Database Configuration (MySQL)
Make sure you have a MySQL instance running on your machine with the following settings (defined in the `application.properties` file):

- **URL:** `jdbc:mysql://localhost:3356/library_jpql`
- **Username:** `root`
- **Password:** `mysqlPW`

> [!NOTE]
> Spring Boot is configured with `spring.jpa.hibernate.ddl-auto=update`. This means all tables (Livro, Autor, Editora, etc.) will be automatically created and updated in your database as soon as the application starts.

### 2. Running the Application
In the root directory of the project, run the following command to start the Spring Boot server:

```bash
./mvnw spring-boot:run
```

The server will start by default on port **8080** (`http://localhost:8080`).

---

## 🔌 Main API Endpoints

In addition to the advanced endpoints documented in the **JPQL Queries** section, the API has basic endpoints for registration and listing.

### 📖 Books (`/livro`)

#### 1. Register a Book
- **Method:** `POST /livro`
- **Request Body (JSON):**
  ```json
  {
    "titulo": "The Lord of the Rings",
    "isbn": "978-0261103252",
    "preco": 49.90,
    "dataPublicacao": "1954-07-29",
    "categoria": "Fantasy",
    "editora": {
      "id": 1
    },
    "autores": [
      {
        "id": 1
      }
    ]
  }
  ```

#### 2. List All Books
- **Method:** `GET /livro`
- **Expected Response:** Returns a JSON Array (list) containing all registered books.

### ✍️ Authors (`/autor`)

#### 1. Register an Author
- **Method:** `POST /autor`
- **Request Body (JSON):**
  ```json
  {
    "nome": "J.R.R. Tolkien",
    "nacionalidade": "British",
    "dataNascimento": "1892-01-03"
  }
  ```

#### 2. List All Authors
- **Method:** `GET /autor`
- **Expected Response:** Returns a JSON Array containing all registered authors.

---

## 📂 Project Structure

The project architecture follows the MVC / REST layered pattern:

- 🗃️ **`model`**: Contains the JPA Entities (`Livro`, `Autor`, `Editora`), representing the database tables.
- 💾 **`repository`**: Spring Data JPA interfaces responsible for database communication and query execution.
- ⚙️ **`service`**: Where the application's business logic resides. It mediates communication between controllers and repositories.
- 🌐 **`controller`**: REST Controllers that expose the API's URLs (endpoints) to the outside world.
- 📦 **`dto`**: *Data Transfer Objects*. Objects focused solely on transferring data between the API and the client, omitting sensitive information.
- 🔄 **`mapper`**: Utility classes to convert database Entities into DTOs and vice-versa.

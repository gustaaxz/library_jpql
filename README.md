# Library Management API

📄 **System Context Document:** [Contexto do Sistema - Gestão de Biblioteca.pdf](./Contexto%20do%20Sistema_%20Gest%C3%A3o%20de%20Biblioteca.pdf)

This is a library management API project developed with **Java 21**, **Spring Boot**, and **Spring Data JPA**, using **MySQL** as the database.

---

## 🚀 Technologies Used

- **Java 21**
- **Spring Boot**
  - Spring Boot Starter Web
  - Spring Data JPA
  - Spring Boot DevTools
- **MySQL** (Driver: `mysql-connector-j`)
- **Lombok** (for boilerplate code reduction)
- **Maven** (Dependency manager)

---

## 🛠️ Environment Setup

### 1. Database Configuration (MySQL)
Make sure you have a MySQL instance running with the following settings (defined in [application.properties](file:///c:/Users/gustavo_hatschbach/Desktop/library/src/main/resources/application.properties)):

- **URL:** `jdbc:mysql://localhost:3356/library_jpql`
- **Username:** `root`
- **Password:** `mysqlPW`

> [!NOTE]
> Spring Boot is configured with `spring.jpa.hibernate.ddl-auto=update`, which means tables will be automatically generated in the database when the application starts.

### 2. Running the Application
In the project root directory, run the command below to start the Spring Boot server:

```bash
./mvnw spring-boot:run
```

The server will start by default on port **8080** (`http://localhost:8080`).

---

## 🔌 API Endpoints

### Books (`/livro`)

#### 1. Create a Book
- **Mapping:** `POST /livro`
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
    }
  }
  ```
- **Expected Response (200 OK):**
  ```json
  {
    "id": 1,
    "titulo": "The Lord of the Rings",
    "isbn": "978-0261103252",
    "preco": 49.90,
    "dataPublicacao": "1954-07-29",
    "categoria": "Fantasy",
    "editora": {
      "id": 1,
      "nome": "HarperCollins"
    }
  }
  ```

#### 2. List All Books
- **Mapping:** `GET /livro`
- **Expected Response (200 OK):** Returns a list containing all registered books.

#### 3. Find Book by Title
- **Mapping:** `GET /livro/{titulo}`
- **Example:** `GET http://localhost:8080/livro/The Lord of the Rings`
- **Expected Response (200 OK):** Returns a list of books matching the specified title.

---

## 📂 Project Structure

- `com.weg.library.model`: JPA Entities (`Livro`, `Autor`, `Editora`).
- `com.weg.library.repository`: Spring Data JPA interfaces for database communication.
- `com.weg.library.service`: Application business logic.
- `com.weg.library.controller`: REST Controllers exposing the endpoints.
- `com.weg.library.dto`: Data Transfer Objects (DTOs) for requests and responses.
- `com.weg.library.mapper`: Mappers to convert entities to DTOs and vice-versa.

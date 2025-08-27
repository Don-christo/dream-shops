# 🛒 dream-shops E-commerce API (Spring Boot)

This is a RESTful API for managing an e-commerce system, built using Spring Boot, Java, and JPA. It supports managing
products, categories, carts, orders, and users.

---

## 📌 Headsup

- This project uses MySQL by default (configured in `application.properties`).
- Sample test data for users and roles are loaded via `package com.codewithiyke.dreamshops.data.DataInitializer`; (or
  you can add your own).
- This is a demo app built for learning purposes; in production, you’d secure the API and use a proper database and
  apply other best practices.
- For testing or quick demo purposes, you can switch to an in-memory H2 database by updating the datasource
  configuration.

```properties
# H2 in-memory database config
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

```

---

## 🚀 Tech Stack

- Java 17+
- Spring Boot (REST API)
- Spring Data JPA (ORM)
- Hibernate (JPA implementation)
- H2 Database (default, in-memory)
- Maven (build tool)
- ModelMapper (DTO mapping)
- JUnit & Mockito (for testing)
- Lombok (for boilerplate reduction)

---

## 📂 Folder Structure

```text
.
├── src/
│   ├── main/
│   │   ├── java/com/codewithiyke/dreamshops/
│   │   │   ├── controller/
│   │   │   ├── data/
│   │   │   ├── dto/
│   │   │   ├── enums/
│   │   │   ├── exceptions/
│   │   │   ├── mapper/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── request/
│   │   │   ├── response/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── DreamShopsApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └──application.properties
│   │
│   └── test/
│   │   ├── java/com/codewithiyke/dreamshops/
│   │   │   ├── controller/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── DreamShopsApplicationTests.java
│
├── pom.xml
└── README.md

```

---

## ⚙️ How to Run the Project

```bash
# Clone the repo
git clone https://github.com/Don-christo/dream-shops.git
cd dream-shops

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

Or run the `DreamShopsApplication` class from your IDE.

# Add your environmental variables
create `.env` file in the root directory and add the following:
PORT=3000

✅ Default Configurations
- Server Port: 8080
- Database: H2 (in-memory)
```

## 🧪 API Endpoints

**Product**

POST `/api/v1/products/add` - Creates a new product

- Create a new product. Requires the following body:

```json
{
  "name": "Laptop",
  "description": "High-end laptop",
  "price": 1500.00,
  "category": "Electronics",
  "brand": "Apple"
}
```

---
**Cart**

GET `/api/v1/carts/{cartId}/my-cart` – Get user cart

POST `/api/v1/cartItems/item/add` – Add item to cart

DELETE `/api/v1/carts/{cartId}/clear` – Remove item from cart

---

**Orders**

GET `/api/v1/orders/{orderId}/order` – Get order by ID

GET `/api/v1/orders/{userId}/orders` – Get all orders for a user

POST `/api/v1/orders/order` – Place a new order

---

# 🧾 Sample curl Requests

✅ **_Get all products_**

```bash
curl http://localhost:9191/api/v1/products/all
```

✅ **_Get products by category_**

```bash
curl http://localhost:9191/api/v1/products/product/{category}/all/products
```

✅ **_Get products by ID_**

```bash
curl http://localhost:9191/api/v1/products/product/{productId}/product
```

✅ **_Create a product_**

```bash
curl -X POST http://localhost:9191/api/v1/products/add \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"High-end laptop","price":1500,"category":"Electronics","brand":"Apple"}'
```

🛠️ Features Implemented

✔ CRUD for Products

✔ Cart management (add/remove items)

✔ Order creation and retrieval

✔ User registration and association with orders

✔ DTO mapping using ModelMapper

✔ JUnit & Mockito tests for services and repositories

✔ Authentication & authorization using Spring Security

📝 **Notes**

- Configure Swagger/OpenAPI for API documentation (optional).
- IDs are auto-incremented on each data creation.


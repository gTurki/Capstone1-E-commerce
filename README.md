# Capstone1 E-Commerce Website

A RESTful e-commerce backend built with **Spring Boot**, modeling users, products, categories, merchants, and merchant stock. It supports core commerce flows such as purchasing, refunds, coupon redemption, balance transfers, and shipping estimation — all backed by in-memory data structures.

> ⚠️ **Note:** This project currently stores all data in-memory (`ArrayList`s inside each service). There is no database connected yet, so all data resets whenever the application restarts. This makes it ideal for learning/demo purposes and easy to extend with a real database (e.g. via Spring Data JPA) later on.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Data Models & Validation](#-data-models--validation)
- [API Reference](#-api-reference)
  - [User](#user-api-v1user)
  - [Category](#category-api-v1category)
  - [Product](#product-api-v1product)
  - [Merchant Stock](#merchant-stock-api-v1merchantstock)
  - [Merchant](#merchant-api-v1merchant)
- [Business Rules](#-business-rules)
- [Future Improvements](#-future-improvements)

---

## ✨ Features

- **User management** — create, update, delete, and list users with role-based accounts (`Admin` / `Customer`) and wallet balances.
- **Product & Category catalog** — full CRUD for products and their categories.
- **Merchant marketplace** — multiple merchants can sell the same product, each with independent stock.
- **Purchasing flow** — buy a product from a specific merchant, with balance and stock checks.
- **Stock management** — top up merchant stock, view low-stock alerts.
- **Product comparison** — compare prices/stock across merchants selling the same product.
- **Budget-based recommendations** — list products a user can currently afford and that are in stock.
- **Peer-to-peer balance transfers** between users.
- **Shipping duration estimation** based on user/merchant region.
- **Refunds** with different payout percentages depending on item condition.
- **Coupon redemption** to top up user balance.
- **Bean validation** on every model (regex-based ID formats, required fields, password rules, etc.).

---

## 🛠 Tech Stack

| Layer            | Technology                          |
|-------------------|--------------------------------------|
| Language           | Java                                |
| Framework          | Spring Boot (Spring Web / Spring MVC) |
| Validation         | Jakarta Bean Validation (`jakarta.validation`) |
| Boilerplate        | Lombok (`@Data`, `@AllArgsConstructor`, `@RequiredArgsConstructor`) |
| Data storage       | In-memory (`ArrayList`, no database) |
| API style          | REST (JSON over HTTP)               |

---

## 📁 Project Structure

```
src/main/java/com/example/capstone1ecommercewebsite/
├── Api/
│   └── ApiResponse.java          # Generic { "message": "..." } response wrapper
├── Controller/
│   ├── UserController.java
│   ├── CategoryController.java
│   ├── ProductController.java
│   ├── MerchantStockController.java
│   └── MerchantController.java
├── Model/
│   ├── User.java
│   ├── Category.java
│   ├── Product.java
│   ├── Merchant.java
│   └── MerchantStock.java
└── Service/
    ├── UserService.java
    ├── CategoryService.java
    ├── ProductService.java
    ├── MerchantStockService.java
    └── MerchantService.java      # Orchestrates cross-entity business logic
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+ (Jakarta EE namespace requires Spring Boot 3.x)
- Maven (or the Maven wrapper `./mvnw` if included in the repo)

### Clone & Run

```bash
git clone https://github.com/gTurki/Capstone1-E-commerce.git
cd Capstone1-E-commerce

# Run with Maven
mvn spring-boot:run

# or, if a wrapper is present
./mvnw spring-boot:run
```

By default, Spring Boot serves the API at:

```
http://localhost:8080
```

### Quick Test

```bash
curl -X POST http://localhost:8080/api/v1/category/add \
  -H "Content-Type: application/json" \
  -d '{"id": "c1", "name": "Electronics"}'
```

---

## 🧩 Data Models & Validation

### User
| Field    | Rules |
|----------|-------|
| `id`       | Required, 3–7 chars, must match `u\d+` (e.g. `u1`, `u23`) |
| `username` | Required, 5–20 chars |
| `password` | Required, 6–25 chars, must contain at least one letter and one digit |
| `email`    | Required, must be a valid email format |
| `role`     | Required, must be `Admin` or `Customer` (case-insensitive) |
| `balance`  | Required, must be positive |

### Category
| Field  | Rules |
|--------|-------|
| `id`   | Required, 3–7 chars, must match `c\d+` (e.g. `c1`) |
| `name` | Required |

### Product
| Field        | Rules |
|--------------|-------|
| `id`         | Required, 3–7 chars, must match `p\d+` (e.g. `p1`) |
| `name`       | Required, 3–20 chars |
| `price`      | Required, must be positive |
| `categoryID` | Required, 3–7 chars |

### Merchant
| Field  | Rules |
|--------|-------|
| `id`   | Required, 3–7 chars, must match `m\d+` (e.g. `m1`) |
| `name` | Required |

### MerchantStock
| Field        | Rules |
|--------------|-------|
| `id`         | Required, 3–7 chars, must match `ms\d+` (e.g. `ms1`) |
| `productID`  | Required |
| `merchantID` | Required |
| `stock`      | Required, minimum value of **10** at creation |

Any validation failure returns **HTTP 400** with the first violation's message.

---

## 📡 API Reference

All responses are JSON. Successful writes return an `ApiResponse` (`{ "message": "..." }`); reads return the requested entity/entities directly.

### User `/api/v1/user`

| Method | Endpoint | Description |
|--------|----------|--------------|
| GET    | `/get` | List all users |
| POST   | `/add` | Add a single user |
| POST   | `/add/multi` | Add a list of users |
| PUT    | `/update/{id}` | Update a user by ID |
| DELETE | `/delete/{id}` | Delete a user by ID |

### Category `/api/v1/category`

| Method | Endpoint | Description |
|--------|----------|--------------|
| GET    | `/get` | List all categories |
| POST   | `/add` | Add a single category |
| POST   | `/add/multi` | Add a list of categories |
| PUT    | `/update/{id}` | Update a category by ID |
| DELETE | `/delete/{id}` | Delete a category by ID |

### Product `/api/v1/product`

| Method | Endpoint | Description |
|--------|----------|--------------|
| GET    | `/get` | List all products |
| POST   | `/add` | Add a single product |
| POST   | `/add/multi` | Add a list of products |
| PUT    | `/update/{id}` | Update a product by ID |
| DELETE | `/delete/{id}` | Delete a product by ID |

### Merchant Stock `/api/v1/merchantstock`

| Method | Endpoint | Description |
|--------|----------|--------------|
| GET    | `/get` | List all merchant stock records |
| POST   | `/add` | Add a single merchant stock record |
| POST   | `/add/multi` | Add a list of merchant stock records |
| PUT    | `/update/{id}` | Update a merchant stock record by ID |
| DELETE | `/delete/{id}` | Delete a merchant stock record by ID |

### Merchant `/api/v1/merchant`

Basic CRUD:

| Method | Endpoint | Description |
|--------|----------|--------------|
| GET    | `/get` | List all merchants |
| POST   | `/add` | Add a single merchant |
| POST   | `/add/multi` | Add a list of merchants |
| PUT    | `/update/{id}` | Update a merchant by ID |
| DELETE | `/delete/{id}` | Delete a merchant by ID |

Commerce operations:

| Method | Endpoint | Description |
|--------|----------|--------------|
| PUT | `/addstock/productid/{productID}/merchantid/{merchantID}/stock/{stock}` | Increase a merchant's stock of a product |
| PUT | `/buy/userid/{userID}/productid/{productID}/merchantid/{merchantID}` | Purchase a product from a merchant (checks balance & stock) |
| GET | `/get/low-stock/{merchantID}/{lowStockAmount}` | Get a merchant's stock items at or below a threshold |
| GET | `/get/merchant-products/{merchantID}` | List distinct products sold by a merchant |
| GET | `/get/compare-merchants/{productID}` | Compare stock across all merchants selling a product |
| GET | `/get/products-within-budget/userid/{userID}` | List in-stock products a user can afford |
| PUT | `/transfer/senderid/{senderID}/receiverid/{receiverID}/amount/{amount}` | Transfer balance from one user to another |
| GET | `/shipping-duration/userid/{userID}/productid/{productID}/merchantid/{merchantID}/user-region/{userRegion}/merchant-region/{merchantRegion}` | Estimate shipping duration |
| PUT | `/refund/userid/{userID}/productid/{productID}/merchantid/{merchantID}/condition/{condition}` | Refund an item (`condition` = `unused` or `used`) |
| PUT | `/apply-discount/{userID}/{code}` | Redeem a coupon code to top up balance |

---

## 📐 Business Rules

- **Purchasing** deducts the product price from the buyer's balance and decrements merchant stock by 1. Fails if balance is insufficient, IDs are invalid, or stock is empty.
- **Shipping duration**
  - Same region → **2 business days**
  - Different regions → **5 business days**
- **Refunds** restore 1 unit of stock and credit the user based on item condition:
  - `unused` → 80% of the product price refunded
  - `used` → 50% of the product price refunded
- **Coupon codes** (case-insensitive) currently supported:
  - `student10` → +10 balance
  - `riyadh20` → +20 balance
  - `tuwaiq50` → +50 balance
- **Balance transfers** require a positive amount, valid distinct sender/receiver IDs, and sufficient sender balance.

---

## 🔭 Future Improvements

- Persist data with a real database (e.g. PostgreSQL/MySQL via Spring Data JPA) instead of in-memory lists.
- Add authentication/authorization (e.g. Spring Security + JWT) instead of relying on a plain `role` field.
- Move hardcoded coupon codes into a configurable/persisted `Coupon` entity.
- Add global exception handling (`@ControllerAdvice`) for cleaner, consistent error responses.
- Add unit and integration tests for services and controllers.
- Add pagination and filtering to `GET` endpoints as data grows.

---

## 👤 Author

**gTurki** — [GitHub](https://github.com/gTurki)

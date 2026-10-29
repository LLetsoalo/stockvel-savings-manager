# Stockvel Savings Manager

**A Java and Spring Boot web application for managing community savings groups.**

> **Project status:** Demonstration application — not production-ready.

## Overview

Stockvel Savings Manager that demonstrates how a community savings group can manage members, monthly contributions, loans, repayments, and financial summaries in one web application.

The application includes a dashboard, individual member contribution histories, loan tracking, and downloadable CSV reports for loans and contributions.

## Features

- **Member management:** Register members and maintain their contact details.
- **Contribution tracking:** Record dated contributions and review monthly and lifetime totals.
- **Member histories:** View individual contribution records.
- **Loan management:** Issue loans using a fixed 25% interest calculation.
- **Repayment tracking:** Record repayments and view outstanding loan balances.
- **Financial reports:** Download contribution and loan summaries as CSV files.
- **Dashboard:** Review key savings group information in one place.

### Example loan calculation

The demo applies a fixed 25% interest charge to each loan.

| Item | Example |
|---|---:|
| Loan principal | R1,000 |
| Interest (25%) | R250 |
| Total repayable | R1,250 |

This being a demonstration calculation, not financial advice.

## Technology stack

- Java 25
- Spring Boot
- Maven
- PostgreSQL
- HTML and CSS
- JavaScript

## Requirements

Before running the application, install:

- Java 25
- PostgreSQL
- GNU Make (optional)

Verify your Java installation:

```bash
java -version
```

## Getting started

### 1. Clone the repository

```bash
git clone https://github.com/LLetsoalo/stockvel-savings-manager.git
cd stockvel-savings-manager
```

If your `pom.xml` is inside a nested `demo` directory, enter it:

```bash
cd demo
```

Run the following commands from the directory containing `pom.xml`.

### 2. Create the PostgreSQL database

Using pgAdmin, create a database named `stockvel_db`.

Alternatively, run this SQL command in PostgreSQL:

```sql
CREATE DATABASE stockvel_db;
```

### 3. Configure the database connection

Set the environment variables in your terminal before starting the application.

**Windows PowerShell:**

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/stockvel_db"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "your-local-password"
```

Replace `your-local-password` with your own local PostgreSQL password.

**macOS/Linux:**

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/stockvel_db"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="your-local-password"
```

### 4. Run the application

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS/Linux:**

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Once the application starts, open:

http://localhost:8080

### 5. Run tests and build

**Windows:**

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

**macOS/Linux:**

```bash
./mvnw test
./mvnw clean package
```

If GNU Make is installed and the project Makefile is configured correctly, you can also use `make run`, `make test`, `make package`, and `make clean`.

## Demo limitations

This project is intended for local evaluation and demonstration.

- There is no authentication, user-role management, or access control.
- Database configuration and schema management are development-oriented.
- The loan interest calculation is fixed at 25%.
- The application has not been hardened for production deployment.

**Do not expose this application to the public internet or use it to store real member or financial information.**

## Future improvements

Potential next steps include:

- User authentication and role-based permissions.
- Configurable contribution schedules and due dates.
- Overdue contribution tracking and notifications.
- Improved financial dashboards and reporting.
- Secure production database configuration.
- Automated tests for contributions, loans, and repayments.

## Author

**LLetsoalo**

GitHub: [@LLetsoalo](https://github.com/LLetsoalo)

---

*This project was developed as a software development portfolio project to demonstrate Java, Spring Boot, database integration, and web application development.*

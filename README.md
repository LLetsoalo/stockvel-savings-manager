# Stockvel Savings Manager Demo

> Demo application — not production-ready software.

Stockvel Savings Manager is a demonstration web application for exploring how a
community savings group can track members, contributions, loans, repayments,
and financial summaries in one place. It provides a dashboard, individual
member contribution histories, and downloadable contributions and loans  reports.

The demo applies a fixed 25% interest charge to each loan. For example, a
principal of R1,000 has a total repayable amount of R1,250.

## Demo limitations

This application is intended for local evaluation and demonstration. It has no
login, roles, or access control, and its database configuration and schema
management are development-oriented. Do not expose it to the public internet or
use it to store real member or financial data as it is just an overview for now.

## Requirements

- Java 25
- PostgreSQL
- GNU Make (optional)

## Set up the database

Create a PostgreSQL database named `stockvel_db`. For example, from `psql`:

```sql
CREATE DATABASE stockvel_db;
```

Configure the connection in the terminal before starting the application.
PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/stockvel_db"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "<your-local-password>"
```

macOS/Linux shell:

```sh
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/stockvel_db"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="<your-local-password>"
```

Use the username and password configured for your own local PostgreSQL
installation. Do not commit credentials to source control.

## Run the demo

From the `demo` project directory on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```sh
./mvnw spring-boot:run
```

If GNU Make is installed, `make run` starts the application as well. Once it
has started, open [http://localhost:8080](http://localhost:8080).

## Demo features

- Register stockvel members and maintain their contact details.
- Record dated contributions and review monthly and lifetime totals.
- Browse each member's contribution history.
- Issue loans with the demo's fixed 25% interest calculation.
- Record repayments and view outstanding loan balances.
- Download contribution and loan summaries as CSV files.

## Useful commands

Run these commands from the `demo` directory:

| Purpose | Windows | macOS/Linux |
| --- | --- | --- |
| Start the app | `.\mvnw.cmd spring-boot:run` | `./mvnw spring-boot:run` |
| Run tests | `.\mvnw.cmd test` | `./mvnw test` |
| Build the executable JAR | `.\mvnw.cmd clean package` | `./mvnw clean package` |
| Clean build output | `.\mvnw.cmd clean` | `./mvnw clean` |

With GNU Make, the corresponding targets are `make run`, `make test`,
`make package`, and `make clean`.

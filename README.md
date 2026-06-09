# Wizard Registry Service

Ministry of Magic — Registered Wizards Management Service.

A Dropwizard 4 REST service for tracking registered magicians, built as a realistic workshop demo for migration exercises.

## Tech Stack

- **Java 25**, **Maven**, **Dropwizard 4.0.2**
- **JDBI3** for data access
- **H2** in-memory database (PostgreSQL-compatible mode)
- **Swagger / OpenAPI** via smoketurner dropwizard-swagger

## Prerequisites

- JDK 25+ (`java -version`)
- Maven 3.8+ (`mvn -version`)

## Build

```bash
mvn clean verify
```

This runs:
- **Unit tests** (11) — service logic with mocked DAO, configuration parsing
- **Integration tests** (11) — full app startup via `DropwizardAppExtension`, CRUD, filtering, status transitions

## Run locally

```bash
mvn clean package -DskipTests
java -jar target/wizard-registry-1.0-SNAPSHOT.jar server src/main/resources/wizard-registry.yml
```

The service starts on:
- **Application**: http://localhost:8080
- **Admin**: http://localhost:8081
- **Swagger UI**: http://localhost:8080/swagger

On startup, the registry seeds 10 famous wizards (Harry Potter, Hermione Granger, etc.).

## API

Base path: `/api/wizards`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/wizards` | Register a new wizard |
| `GET` | `/api/wizards` | List all wizards |
| `GET` | `/api/wizards?house=GRYFFINDOR` | Filter by house |
| `GET` | `/api/wizards?status=ACTIVE` | Filter by status |
| `GET` | `/api/wizards?q=Potter` | Search by name |
| `GET` | `/api/wizards/{id}` | Get wizard by ID |
| `PUT` | `/api/wizards/{id}` | Update wizard (patronus, wand) |
| `PATCH` | `/api/wizards/{id}/status` | Change status (send plain text body) |
| `DELETE` | `/api/wizards/{id}` | Deregister (soft delete → SUSPENDED) |

### Quick smoke test

```bash
# List all wizards
curl -s http://localhost:8080/api/wizards | jq .

# Register a new wizard
curl -s -X POST http://localhost:8080/api/wizards \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Albus",
    "lastName": "Dumbledore",
    "dateOfBirth": "1881-07-01",
    "house": "GRYFFINDOR",
    "patronus": "Phoenix",
    "wandWood": "Elder",
    "wandCore": "PHOENIX_FEATHER",
    "wandLengthInches": 15.0
  }' | jq .

# Search by name
curl -s 'http://localhost:8080/api/wizards?q=Hermione' | jq .

# Health check
curl -s http://localhost:8081/healthcheck | jq .
```

### Status page

A plain HTML status page is available at http://localhost:8080/ministry/status.

## Valid values

**Houses**: `GRYFFINDOR`, `HUFFLEPUFF`, `RAVENCLAW`, `SLYTHERIN`, `NONE`

**Wand cores**: `PHOENIX_FEATHER`, `DRAGON_HEARTSTRING`, `UNICORN_HAIR`

**Statuses**: `ACTIVE`, `SUSPENDED`, `DECEASED`, `MISSING`

**Status transitions**:
```
ACTIVE    → SUSPENDED, MISSING, DECEASED
SUSPENDED → ACTIVE, DECEASED
MISSING   → ACTIVE, DECEASED
DECEASED  → (terminal)
```

## Moderne CLI — LST Build

To build an LST for use with OpenRewrite recipes:

```bash
mod build .
```

This produces an LST artifact in `.moderne/build/` that can be used with `mod run` to execute recipes.

## Project Structure

```
org.ministry.magic
├── WizardRegistryApplication    — DW4 Application (initialize + run)
├── WizardRegistryConfiguration  — Configuration with DataSourceFactory + Swagger
├── api/                         — Request/response DTOs
├── bundle/                      — LoggingConfigurationFactoryFactory
├── core/                        — Domain model (Wizard, House, WandCore, RegistrationStatus)
├── db/                          — JDBI3 DAO + RowMapper
├── filter/                      — AuditLogFilter (jakarta.servlet.Filter)
├── health/                      — DatabaseHealthCheck, WizardRegistryHealthCheck
├── managed/                     — WizardRegistryManaged (schema + seed data)
├── resources/                   — WizardResource (JAX-RS)
├── service/                     — WizardService (business rules)
└── servlet/                     — MinistryStatusServlet (plain HTML)
```

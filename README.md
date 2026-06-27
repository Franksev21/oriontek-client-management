# OrionTek — Client Management System

Sistema de gestión de clientes para OrionTek. Cada cliente puede tener N direcciones.

## Stack Técnico

| Capa | Tecnología |
|---|---|
| Backend | Java 21 + Spring Boot 3.2 |
| Patrón | CQRS (Commands / Queries separados) |
| Base de datos | PostgreSQL 16 |
| Migraciones | Flyway |
| Frontend | React 18 + Vite + TailwindCSS |
| Contenedores | Docker + Docker Compose |
| Tests | JUnit 5 + Mockito + Testcontainers |
| Docs API | Swagger / OpenAPI 3 |

## Java 21 Features utilizados

- **Records** — DTOs inmutables (Commands, Queries, Responses)
- **Sealed Classes** — Resultados tipados (`ClientCommandResult`)
- **Pattern Matching** en switch expressions
- **Virtual Threads** — `spring.threads.virtual.enabled=true`
- **Text Blocks** — queries SQL en tests

## Levantar el proyecto (un solo comando)

```bash
docker-compose up --build
```

Esto levanta:
- PostgreSQL en `localhost:5432`
- Backend en `http://localhost:8080`
- Frontend en `http://localhost:80`

## URLs importantes

| Servicio | URL |
|---|---|
| Frontend | http://localhost |
| API | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

## Endpoints principales

```
GET    /api/v1/clients              — Listar clientes (búsqueda + paginación)
GET    /api/v1/clients/{id}         — Detalle con direcciones
GET    /api/v1/clients/stats        — Estadísticas
POST   /api/v1/clients              — Crear cliente
PUT    /api/v1/clients/{id}         — Actualizar cliente
DELETE /api/v1/clients/{id}         — Soft delete

POST   /api/v1/clients/{id}/addresses           — Agregar dirección
PUT    /api/v1/clients/{id}/addresses/{addrId}  — Actualizar dirección
DELETE /api/v1/clients/{id}/addresses/{addrId}  — Eliminar dirección
```

## Desarrollo local (sin Docker)

### Backend
```bash
cd backend
# Requiere PostgreSQL corriendo en localhost:5432
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Abre http://localhost:5173
```

### Correr tests
```bash
cd backend
mvn test
```

## Arquitectura CQRS

```
presentation/
  rest/ClientController     ← Recibe HTTP, delega a handlers

application/
  commands/                 ← Records inmutables (CreateClientCommand...)
  queries/                  ← Records inmutables (GetAllClientsQuery...)
  handlers/
    command/                ← Escribe: ClientCommandHandler
    query/                  ← Lee: ClientQueryHandler

domain/
  exception/                ← ClientNotFoundException, DuplicateEmailException

infrastructure/
  persistence/
    entity/                 ← ClientEntity, AddressEntity (JPA)
    repository/             ← ClientJpaRepository, AddressJpaRepository
  config/                   ← AppConfig (CORS, OpenAPI), GlobalExceptionHandler
```

# BookPortal V2 - AI Context

## Project Overview

Full-stack book management portal with Spring Boot backend and React frontend.

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.3.4 (Java 17)
- **Database**: PostgreSQL (dev), H2 (test)
- **Security**: Spring Security + JWT
- **API Docs**: OpenAPI/Swagger (springdoc-openapi)
- **Testing**: JUnit 5, Mockito, JaCoCo

### Frontend
- **Framework**: React 19
- **Language**: TypeScript
- **Build Tool**: Vite 7
- **Styling**: Tailwind CSS + Styled Components
- **Routing**: React Router DOM 7
- **HTTP Client**: Axios

## Project Structure

```
bookportalv2/
├── backend/
│   ├── src/main/java/com/bookportal/backend/
│   │   ├── config/       # Configuration classes
│   │   ├── controller/   # REST controllers
│   │   ├── dto/          # Data Transfer Objects
│   │   ├── entity/       # JPA entities
│   │   ├── exception/    # Exception handlers
│   │   ├── mapper/       # DTO <-> Entity mappers
│   │   ├── model/        # Domain models
│   │   ├── repository/   # JPA repositories
│   │   ├── security/     # Security/JWT config
│   │   ├── service/      # Business logic
│   │   └── util/         # Utilities
│   └── pom.xml
├── frontend/
│   ├── src/              # React source
│   └── package.json
├── iaContext/            # AI context files
│   └── auth_workflow_summary.md
├── docker-compose.yml
├── docker-compose.prod.yml
└── Makefile
```

## Development URLs

| Service | URL |
|---------|-----|
| Frontend (dev) | http://localhost:5173 |
| Backend (dev) | http://localhost:8080 |
| H2 Console | http://localhost:8080/h2-console |
| OpenAPI Docs | http://localhost:8080/swagger-ui.html |

## Available Commands

```bash
# Makefile commands
make dev-local      # Start frontend + backend locally
make dev-docker     # Start with Docker
make prod-docker    # Production Docker
make stop           # Stop containers
make clean          # Clean up everything
make logs           # Show container logs

# Frontend
cd frontend && npm run dev      # Dev server
cd frontend && npm run build    # Production build
cd frontend && npm run lint     # Lint code

# Backend
cd backend && ./mvnw spring-boot:run  # Run app
cd backend && ./mvnw test              # Run tests
cd backend && ./mvnw test -DskipTests  # Build without tests
```

## Environment Variables

- **Backend**: `backend/src/main/resources/application.properties`
- **Frontend**: `frontend/.env` (see `.env.docker-dev` for reference)
- **Example**: `.env.prod.example`

## Operational Workflow

For any task, use these 3 sub-agents in sequence:

### 1. test_writer
Writes unit/integration tests first to define the expected behavior.

### 2. code_implementer
Implements the feature to make tests pass.

### 3. code_refactorer
Improves code quality, readability, and performance while maintaining test pass.

---

**Important**: Always write tests first, then implement, then refactor. Never skip the test phase.

## Additional Context Files

- `iaContext/auth_workflow_summary.md` - Authentication flow details
- `SEED_DATA_SUMMARY.md` - Database seed data
- `DOCKER-README.md` - Docker setup guide
- `POSTGRESQL_SETUP.md` - PostgreSQL configuration

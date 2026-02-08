# Books Recommendation Portal - Multi-Environment Setup

## Quick Start

### Local Development
```bash
make dev-local
```

Access your application:
- **Frontend**: http://localhost:5173 (Vite dev server)
- **Backend API**: http://localhost:8080/api
- **H2 Console**: http://localhost:8080/h2-console

### Docker Development (with hot-reloading)
```bash
make dev-docker
```

Access your application:
- **Frontend**: http://localhost:5173 (Vite dev server with hot-reload)
- **Backend API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432 (bookportal/bookportal)
- **H2 Console**: Not available in Docker environments

**Test Accounts (Auto-seeded on startup):**
- Admin: `admin` / `Password1!` (1 book)
- User: `user` / `Password1!` (2 books)

### Docker Production
```bash
make prod-docker
```

Access your application:
- **Frontend**: http://localhost:80 (nginx serving production build)
- **Backend API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432 (credentials from .env.prod)

## Architecture

### Frontend (React + Vite)
- **Local Development**: Native npm run dev (port 5173)
- **Docker Development**: Node.js 20 Alpine with Vite dev server + proxy
- **Docker Production**: Multi-stage build with nginx:alpine
- **Hot-reloading**: Volume mounts for Docker, native for local
- **Environment Detection**: Automatic API URL resolution

### Backend (Spring Boot)
- **Runtime**: Eclipse Temurin JDK 17
- **Database**: H2 (local), PostgreSQL (Docker dev/prod)
- **Profiles**: `default` (local), `dev` (Docker), `prod` (Docker production)
- **CORS**: Environment-specific configuration
- **Migrations**: Flyway for PostgreSQL environments
- **Connection Pooling**: HikariCP in production
- **Access**: H2 Console only in local development

## Container Configuration

### Development Docker Compose
- **Frontend**: Port 5173, volume mounts for source code
- **Backend**: Port 8080, volume mounts for source code
- **PostgreSQL**: Port 5432, no data persistence (fresh each restart)
- **Network**: Internal bridge for service communication
- **Environment**: Docker-friendly service discovery

### Production Docker Compose
- **Frontend**: Port 80, optimized nginx serving
- **Backend**: Port 8080, production-optimized JAR
- **PostgreSQL**: Port 5432, persistent data volume
- **Security**: Environment variables from .env.prod file
- **Network**: Internal bridge for service communication

## Environment Variables

### Frontend
- `VITE_API_URL=http://backend:8080/api` - API endpoint

### Backend
- `SPRING_PROFILES_ACTIVE=dev` - Spring profile
- `FRONTEND_URL=http://frontend:3000` - CORS configuration

## Development Workflow

1. **Start containers**: `docker compose up --build`
2. **Make code changes**: Files are synced automatically
3. **Frontend**: Hot-reloads on save
4. **Backend**: Restart Spring Boot manually or wait for recompilation
5. **Stop**: `docker compose down`

## Production Deployment

1. **Build images**: `docker compose -f docker-compose.prod.yml build`
2. **Start services**: `docker compose -f docker-compose.prod.yml up -d`
3. **Stop services**: `docker compose -f docker-compose.prod.yml down`

## Makefile Commands

```bash
# Start environments
make dev-local      # Local development
make dev-docker     # Docker development with hot-reload
make prod-docker    # Docker production

# Utility commands
make stop           # Stop all containers
make clean          # Clean up containers and images
make logs           # Show container logs
make help           # Show all available commands
```

## Development Seed Data

Docker development automatically seeds test data on startup. See [DOCKER_DEV_SEED.md](DOCKER_DEV_SEED.md) for detailed information.

**Quick Reference:**
- Admin: `admin` / `Password1!` (1 book)
- User: `user` / `Password1!` (2 books)

## Traditional Docker Commands (for reference)

```bash
# View logs
docker compose logs -f frontend
docker compose logs -f backend

# Execute commands in containers
docker compose exec frontend npm test
docker compose exec backend ./mvnw test

# Rebuild specific service
docker compose up --build frontend

# Clean up
docker compose down --volumes --remove-orphans
```

## Troubleshooting

- **Port conflicts**: Check if ports 5173/8080 are available
- **Node version issues**: Uses Node.js 20 for Vite 7+ compatibility
- **Hot-reload not working**: Verify volume mounts in docker-compose.yml
- **CORS errors**: Check `VITE_API_URL` and backend CORS settings
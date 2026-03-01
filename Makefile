.PHONY: help dev-local dev-docker prod-docker stop clean logs

# Help target
help:
	@echo "Available commands:"
	@echo "  dev-local    - Start frontend and backend locally"
	@echo "  dev-docker   - Start development containers"
	@echo "  prod-docker  - Start production containers"
	@echo "  stop         - Stop all containers"
	@echo "  clean        - Clean up containers and images"
	@echo "  logs         - Show container logs"

# Local development
dev-local:
	@echo "Starting local development..."
	@echo "Frontend: http://localhost:5173"
	@echo "Backend:  http://localhost:8080"
	@echo "H2 Console: http://localhost:8080/h2-console"
	cd frontend && npm run dev &
	cd backend && ./mvnw spring-boot:run

# Docker development  
dev-docker:
	@echo "Starting Docker development..."
	@echo "Frontend: http://localhost:5173"
	@echo "Backend:  http://localhost:8080"
	@echo "H2 Console: http://localhost:8080/h2-console"
	docker compose -f docker-compose.yml up --build

# Docker production
prod-docker:
	@echo "Starting Docker production..."
	@echo "Frontend: http://localhost:80"
	@echo "Backend:  http://localhost:8080"
	docker compose -f docker-compose.prod.yml up --build -d

# Utility targets
stop:
	@echo "Stopping all containers..."
	docker compose -f docker-compose.yml down || true
	docker compose -f docker-compose.prod.yml down || true

clean:
	@echo "Cleaning up..."
	docker compose -f docker-compose.yml down --volumes --remove-orphans || true
	docker compose -f docker-compose.prod.yml down --volumes --remove-orphans || true
	docker system prune -f

logs:
	@echo "Showing logs..."
	@echo "Usage: docker compose logs [service]     # All or specific service"
	@echo "       docker compose logs -f backend    # Follow backend logs"
	@echo "       docker compose logs -t            # Show timestamps"
	docker compose logs -f
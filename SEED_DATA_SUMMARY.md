# Seed Data Implementation Summary

## Overview
Successfully implemented automatic seed data for Docker development environment that runs every time containers start.

## Test Accounts

### Admin User
- **Username**: `admin`
- **Login Password**: `Password1!`
- **Stored**: BCrypt hash in database
- **Role**: ROLE_ADMIN
- **Books**: 1
  - The Great Gatsby by F. Scott Fitzgerald

### Regular User  
- **Username**: `user`
- **Login Password**: `Password1!`
- **Stored**: BCrypt hash in database
- **Role**: ROLE_USER
- **Books**: 2
  - 1984 by George Orwell
  - Mockingbird by Harper Lee

## Files Created

### 1. Seed SQL Script
**File**: `backend/src/main/resources/db/seed/dev-seed-data.sql`
- Creates tables if not exist
- Truncates existing data
- Inserts roles, users, user_roles, and books
- Uses plain text passwords
- Resets sequences for clean IDs

### 2. Shell Script (Optional/Backup)
**File**: `backend/scripts/seed-dev-data.sh`
- Alternative execution method
- Waits for PostgreSQL to be ready
- Can be used for more complex seeding logic

### 3. Documentation
**File**: `DOCKER_DEV_SEED.md`
- Complete guide for seed data
- Usage examples
- Customization instructions
- Troubleshooting tips

## Docker Compose Configuration

**Updated**: `docker-compose.yml`

```yaml
postgres:
  volumes:
    - ./backend/src/main/resources/db/seed/dev-seed-data.sql:/docker-entrypoint-initdb.d/01-seed-data.sql
```

The PostgreSQL Docker image automatically executes scripts in `/docker-entrypoint-initdb.d/` on startup.

## How It Works

1. **Container Starts**: `make dev-docker`
2. **PostgreSQL Initialization**: Database created
3. **Auto-Execution**: PostgreSQL runs scripts in initdb.d
4. **Schema Creation**: Tables created if not exist
5. **Data Cleanup**: Existing data truncated
6. **Seed Insertion**: Fresh test data inserted
7. **Ready**: Database ready for development

## Key Features

✅ **Fresh Data Every Time**: No persistence between restarts  
✅ **Auto-Execution**: Runs automatically on container start  
✅ **Self-Contained**: Creates schema if needed  
✅ **BCrypt Hashed Passwords**: Secure storage, login with Password1!  
✅ **Consistent IDs**: Sequences reset for predictable IDs  
✅ **Docker Only**: Does not affect local or production  

## Usage

### Start Development
```bash
make dev-docker
```

### Login Test
```bash
# Admin login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Password1!"}'

# User login  
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"Password1!"}'
```

### View Books
```bash
# Admin's books (1 book)
curl http://localhost:8080/api/user/1/books

# User's books (2 books)
curl http://localhost:8080/api/user/2/books
```

## Verification

Successfully tested:
- ✅ Containers start without errors
- ✅ Seed script executes automatically
- ✅ Tables created successfully
- ✅ Users inserted with roles
- ✅ Books associated with users
- ✅ BCrypt hashed passwords verified
- ✅ Login with plain text works
- ✅ Fresh data on each restart

## Environment Comparison

| Feature | Local | Docker Dev | Docker Prod |
|---------|-------|------------|-------------|
| Database | H2 | PostgreSQL | PostgreSQL |
| Seed Data | No | Yes (auto) | No |
| Persistence | No | No | Yes |
| Passwords | N/A | Password1! | From .env |

## Notes

- Seed data runs only in Docker development environment
- Local development (H2) does not auto-seed
- Production environment does not auto-seed
- Each `make dev-docker` starts with fresh seeded data
- Data is consistent and predictable for testing

## Troubleshooting

If seed data doesn't appear:
1. Check PostgreSQL logs: `docker compose logs postgres`
2. Look for: "running /docker-entrypoint-initdb.d/01-seed-data.sql"
3. Verify script mounted: `docker compose exec postgres ls /docker-entrypoint-initdb.d/`
4. Restart: `make dev-docker`
# Docker Development Seed Data

## Overview

Docker development environment automatically seeds test data every time containers start. This provides a consistent testing environment with pre-populated users and books.

## Test Accounts

### Admin User
- **Username**: `admin`
- **Password**: `Password1!`
- **Role**: ROLE_ADMIN
- **Books**: 1 book
  - The Great Gatsby by F. Scott Fitzgerald

### Regular User
- **Username**: `user`
- **Password**: `Password1!`
- **Role**: ROLE_USER
- **Books**: 2 books
  - 1984 by George Orwell
  - Mockingbird by Harper Lee

## How It Works

### Automatic Execution
The seed data script (`dev-seed-data.sql`) automatically executes when Docker development containers start:

1. PostgreSQL container starts
2. Database `bookportal` is created
3. Script in `/docker-entrypoint-initdb.d/` runs automatically
4. Tables are created (if not exist)
5. Existing data is truncated (fresh start)
6. Seed data is inserted

### Key Features

**Fresh Data Every Time**
- Data is not persisted between container restarts
- Each `make dev-docker` starts with clean, seeded data
- Perfect for consistent testing

**Self-Contained Script**
- Creates schema if needed
- Handles table dependencies
- Resets sequences for clean IDs

**BCrypt Hashed Passwords**
- Passwords stored as BCrypt hashes in database
- Login with plain text: `Password1!`
- Spring Security handles hashing automatically

## File Locations

- **Seed Script**: `backend/src/main/resources/db/seed/dev-seed-data.sql`
- **Docker Compose**: Mounts script to PostgreSQL init directory
- **Execution**: Automatic on container startup

## Usage Examples

### Login as Admin
```bash
# Via API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Password1!"}'
```

### Login as User
```bash
# Via API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"Password1!"}'
```

### View User Books
```bash
# Get user ID 1 (admin) books
curl http://localhost:8080/api/user/1/books

# Get user ID 2 (user) books
curl http://localhost:8080/api/user/2/books
```

## Customizing Seed Data

To modify the seed data:

1. Edit: `backend/src/main/resources/db/seed/dev-seed-data.sql`
2. Modify INSERT statements
3. Run: `make dev-docker` (restarts with new data)

### Example: Add More Books
```sql
-- Add another book for admin
INSERT INTO books (title, author, review, user_id)
SELECT 'New Book', 'Author Name', 'Book description here.', id
FROM users WHERE username = 'admin';
```

### Example: Add New User
```sql
-- Insert new user (use BCrypt hashed password)
-- To generate hash: use BCryptPasswordEncoder.encode("Password1!")
INSERT INTO users (username, password) 
VALUES ('newuser', '$2a$10$4OynpGPk.9tGJmym7hhFNu5YGJj4gRayY02p7ZTMK4XI63/FOMtMO');

-- Assign role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'newuser' AND r.name = 'ROLE_USER';
```

## Troubleshooting

### Seed Data Not Appearing
1. Check PostgreSQL logs: `docker compose logs postgres`
2. Look for "running /docker-entrypoint-initdb.d/" message
3. Verify script is mounted: `docker compose exec postgres ls /docker-entrypoint-initdb.d/`

### Data Persists Between Restarts
If data persists when it shouldn't:
- Docker might be caching volumes
- Run: `make clean` to remove all volumes
- Then: `make dev-docker` for fresh start

### Password Login Fails
- **Login with**: `Password1!` (plain text)
- **Database stores**: BCrypt hashed version
- **Both users have same login password**: `Password1!`
- **Note**: Passwords are hashed for security, Spring Security handles the comparison

## Notes

- Seed data is for **Docker Development only**
- Local development (H2) does not auto-seed
- Production environment does not auto-seed
- Data is reset on every `make dev-docker` execution
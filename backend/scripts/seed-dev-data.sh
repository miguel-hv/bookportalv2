#!/bin/bash

# Docker Development Database Seeding Script
# This script runs automatically when containers start
# It waits for PostgreSQL to be ready, then seeds the database

set -e

echo "Waiting for PostgreSQL to be ready..."

# Wait for PostgreSQL to be ready
until pg_isready -h postgres -p 5432 -U bookportal; do
  echo "PostgreSQL is not ready yet. Retrying in 2 seconds..."
  sleep 2
done

echo "PostgreSQL is ready!"

echo "Seeding development data..."

# Execute the seed SQL file
psql -h postgres -p 5432 -U bookportal -d bookportal -f /docker-entrypoint-initdb.d/dev-seed-data.sql

echo "Development data seeded successfully!"
echo ""
echo "Test Accounts:"
echo "  Admin: username='admin', password='Password1!'"
echo "  User:  username='user', password='Password1!'"
echo ""

# Keep container running (if needed) or exit
# This script can be used as an entrypoint or init script
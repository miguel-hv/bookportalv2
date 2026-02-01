# Base
FROM node:20-alpine AS base
WORKDIR /app

# ------------------------------
# Development stage
# ------------------------------
FROM base AS dev
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5173"]

# ------------------------------
# Build stage
# ------------------------------
FROM base AS build
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

# ------------------------------
# Production stage
# ------------------------------
FROM nginx:alpine AS prod
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
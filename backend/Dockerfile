# Base
FROM eclipse-temurin:17-jdk AS base
WORKDIR /app

# ------------------------------
# Development stage
# ------------------------------
FROM base AS dev
COPY . .
RUN ./mvnw dependency:go-offline
CMD ["./mvnw", "spring-boot:run"]

# ------------------------------
# Build stage
# ------------------------------
FROM base AS build
COPY . .
RUN ./mvnw clean package -DskipTests

# ------------------------------
# Production stage
# ------------------------------
FROM eclipse-temurin:17-jdk AS prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

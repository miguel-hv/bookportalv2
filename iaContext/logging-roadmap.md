# Logging Implementation Roadmap

## Current State
- Spring Boot 3.3.4 (Java 17)
- No logging implementation yet
- Uses SLF4J + Logback (default via `spring-boot-starter-logging`)

---

## Step 1: Add Dependencies ✅

Add Logstash encoder for JSON structured logging:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

---

## Step 2: Configure Logback (logback-spring.xml) ✅

Create `backend/src/main/resources/logback-spring.xml`:
- Console appender (human-readable for dev)
- JSON file appender (for production)
- Log rotation settings
- Environment-specific profiles (dev vs prod)

---

## Step 3: Create Logging Filter ✅

Create `LoggingFilter` in `config/` package:
- Extend `OncePerRequestFilter`
- Log every incoming request (method, path, params)
- Log response status and duration
- Add MDC fields: `traceId`, `userId`, `ip`, `method`

---

## Step 4: Add MDC to Security ✅

In JWT filter or security config:
- Extract user info from token
- Add `userId` to MDC
- Add `traceId` (generate if not present)

---

## Step 5: Update Controllers/Services ✅

Add meaningful logs to key areas:
- Auth: login success/failure
- CRUD operations: created, updated, deleted
- Errors: exception details (no sensitive data!)
- Business milestones

---

## Step 6: Test & Verify 🔄

- Run application
- Verify console logs (dev)
- Verify JSON file logs (prod profile)
- Check log rotation works

---

## Industry Standards Applied

| Practice | Implementation |
|----------|----------------|
| Structured JSON logs | Logstash encoder |
| Request tracing | MDC with traceId |
| User context | MDC with userId |
| Log rotation | TimeBasedRollingPolicy |
| Dev vs Prod | Spring profiles |
| No sensitive data | Avoid logging passwords, tokens |

---

## Notes

- **Spring Boot 3.4+** has native structured logging (ECS, GELF, Logstash formats)
- Current version is 3.3.4, so using Logstash encoder
- Always use SLF4J API (not Logback directly) for portability

# Multi-stage Dockerfile for building and running the ecomarket backend
# Stage 1: Builder (build + run tests)
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy everything and build. Using full copy keeps it simple; for faster caching
# you can copy pom.xml and download dependencies separately.
COPY . .

# Ensure Maven runs non-interactively and runs tests (no -DskipTests)
RUN mvn -B clean package

# Stage 2: Runtime
FROM eclipse-temurin:23-jre

WORKDIR /app

# Copy the fat JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose default port (the app will respect server.port property or env var)
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]

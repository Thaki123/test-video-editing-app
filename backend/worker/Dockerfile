# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q package -DskipTests

# Runtime stage with CUDA
FROM nvidia/cuda:12.2.0-runtime-ubuntu22.04
WORKDIR /app
RUN apt-get update && apt-get install -y openjdk-21-jre-headless && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

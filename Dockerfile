# Multi-stage build for Spring Boot app
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR_FILE=target/devops-app-1.0.0.jar
COPY --from=builder /workspace/${JAR_FILE} /app/app.jar
EXPOSE 8080
USER 1000
ENTRYPOINT ["java","-jar","/app/app.jar"]

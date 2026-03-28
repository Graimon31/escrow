FROM gradle:8.14.3-jdk21 AS builder
ARG SERVICE_NAME=deal-service
WORKDIR /workspace
COPY backend/settings.gradle backend/build.gradle backend/gradle.properties ./
COPY backend/services ./services
RUN gradle :services:${SERVICE_NAME}:bootJar --no-daemon

FROM eclipse-temurin:21-jre
ARG SERVICE_NAME=deal-service
WORKDIR /app
COPY --from=builder /workspace/services/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /workspace
COPY backend/settings.gradle backend/build.gradle backend/gradle.properties ./
COPY backend/services ./services
RUN gradle :services:deal-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/services/deal-service/build/libs/deal-service-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

FROM gradle:8.14.3-jdk21 AS builder
ARG SERVICE_NAME=deal-service
ENV GRADLE_OPTS="-Dhttps.protocols=TLSv1.2 -Dorg.gradle.internal.http.connectionTimeout=120000 -Dorg.gradle.internal.http.socketTimeout=120000"
WORKDIR /workspace
COPY backend/settings.gradle backend/build.gradle backend/gradle.properties ./
COPY backend/services ./services
RUN for attempt in 1 2 3; do \
      gradle :services:${SERVICE_NAME}:bootJar --no-daemon && break; \
      echo "Gradle build failed for ${SERVICE_NAME} (attempt ${attempt}/3), retrying..."; \
      if [ "${attempt}" = "3" ]; then exit 1; fi; \
      sleep 5; \
    done

FROM eclipse-temurin:21-jre
ARG SERVICE_NAME=deal-service
WORKDIR /app
COPY --from=builder /workspace/services/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

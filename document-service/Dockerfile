FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY document-service ./document-service
RUN mkdir -p api-gateway auth-service deal-service payment-service notification-service audit-service \
    && chmod +x gradlew && ./gradlew :document-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl && mkdir -p /app/uploads
COPY --from=build /app/document-service/build/libs/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]

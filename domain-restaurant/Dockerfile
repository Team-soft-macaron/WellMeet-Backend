# Stage 1: Build
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :domain-restaurant:bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/domain-restaurant/build/libs/*.jar app.jar

EXPOSE 8083

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+UseG1GC", "-jar", "app.jar"]

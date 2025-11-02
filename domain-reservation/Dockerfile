FROM gradle:8.5-jdk21 AS build

WORKDIR /app

COPY settings.gradle .
COPY build.gradle .
COPY gradle.properties .

COPY domain-common/build.gradle domain-common/
COPY domain-reservation/build.gradle domain-reservation/

COPY domain-common/src domain-common/src
COPY domain-reservation/src domain-reservation/src

RUN gradle :domain-reservation:bootJar --no-daemon --parallel -x test

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/domain-reservation/build/libs/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN useradd -m -u 1001 appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8085

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=docker"]

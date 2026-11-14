# syntax=docker/dockerfile:1
#
# Build context must be money-pusher/ (the Gradle root), not this directory:
#     docker build -f clicker-service/Dockerfile -t money-pusher/clicker-service .
#
# docker-compose.yml already passes the right context.

# ---------- build stage ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# gradlew is committed with CRLF line endings and without the executable bit
# (typical Windows checkout). Fix both, otherwise the shebang becomes
# "/bin/sh^M" and the script is not executable.
COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Only bootJar is invoked, so build/libs contains exactly one jar
# (the plain -jar task is not a dependency of bootJar).
RUN --mount=type=cache,target=/root/.gradle,sharing=locked \
    ./gradlew :clicker-service:bootJar --no-daemon -x test

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre-alpine

# Never run the application as root.
RUN addgroup -S app && adduser -S -G app app

WORKDIR /app
COPY --from=build /workspace/clicker-service/build/libs/*.jar app.jar

USER app
EXPOSE 8082

# MaxRAMPercentage makes the JVM respect the container memory limit instead of
# sizing the heap from the host's total RAM.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]

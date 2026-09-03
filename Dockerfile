# syntax=docker/dockerfile:1

# --- Build ---
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

# --- Runtime ---
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app
RUN groupadd --system spring && useradd --system --gid spring spring
COPY --from=build /build/target/*.jar app.jar
USER spring:spring
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

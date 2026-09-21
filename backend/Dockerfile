# Build stage
FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Runtime stage - Helvetica (used by the PDF export) is one of the 14 standard PDF fonts and
# needs no font files installed, so a bare JRE image is sufficient.
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=build /app/target/course-eligibility-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

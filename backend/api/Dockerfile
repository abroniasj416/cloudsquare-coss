FROM gradle:9.3.0-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8081

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

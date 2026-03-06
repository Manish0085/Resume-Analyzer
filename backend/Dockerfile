# Step 1: Build stage

FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q -e -C -DskipTests dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Step 2: Run stage

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

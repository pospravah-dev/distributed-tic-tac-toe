FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UseStringDeduplication", "-XX:+AlwaysPreTouch", "-jar", "app.jar"]

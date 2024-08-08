FROM maven:3.9.8-amazoncorretto-17 AS build

WORKDIR /app

COPY . /app

RUN mvn clean package

FROM openjdk:17-jdk-buster

EXPOSE 8080

COPY --from=build /app/target/pokedex-0.0.1-SNAPSHOT.jar /app/pokedex-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/pokedex-0.0.1-SNAPSHOT.jar"]
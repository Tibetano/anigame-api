FROM maven:3.9.6 AS build

COPY ./pom.xml ./pom.xml
COPY ./src ./src

RUN mvn install -DskipTests

FROM openjdk:21-slim

WORKDIR /app

COPY --from=build /target/app.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
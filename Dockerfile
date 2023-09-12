FROM openjdk:17-jdk-slim-buster AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

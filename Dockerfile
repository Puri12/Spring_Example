FROM openjdk:17-alpine
COPY ./build/libs/advanced-*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
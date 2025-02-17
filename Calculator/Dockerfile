FROM openjdk:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE=target/Calculator-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]
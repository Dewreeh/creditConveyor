FROM openjdk:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE=target/Gateway-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8120
ENTRYPOINT ["java", "-jar", "application.jar"]
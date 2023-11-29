FROM openjdk:21
WORKDIR /
ADD target/api-users-0.0.1-SNAPSHOT.jar //
EXPOSE 8080
ENTRYPOINT [ "java", "-Dspring.profiles.active=mysql", "-jar", "/api-users-0.0.1-SNAPSHOT.jar"]
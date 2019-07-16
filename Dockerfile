FROM openjdk:8-alpine

COPY target/scala-2.12/ /opt

CMD ["java", "-jar", "/opt/qa-assembly-0.1.0-SNAPSHOT.jar"]
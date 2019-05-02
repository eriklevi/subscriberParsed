FROM openjdk:8-jdk-alpine
MAINTAINER Erik Levi <levi.erik@gmail.com>
ADD target/subscriberParsed-0.0.1-SNAPSHOT.jar subscriber-parsed.jar
ENTRYPOINT ["java", "-jar", "/subscriber-parsed.jar"]
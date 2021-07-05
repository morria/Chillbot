FROM openjdk:11
MAINTAINER Andrew S. Morrison "asm@trapezoid.work"

COPY target /usr/src/myapp
WORKDIR /usr/src/myapp
CMD ["java", "-jar", "Chillbot-1.0-SNAPSHOT-jar-with-dependencies.jar"]

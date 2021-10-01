FROM openjdk:15

WORKDIR /app/

COPY src/*.java ./

RUN javac *.java

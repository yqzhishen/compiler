FROM openjdk:15

WORKDIR /app/

COPY lexer/src/*.java ./

RUN javac *.java

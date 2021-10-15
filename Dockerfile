FROM openjdk:17
WORKDIR /app/
COPY lexer/src/*.java ./
RUN javac *.java

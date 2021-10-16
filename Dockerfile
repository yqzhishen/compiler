FROM openjdk:17
WORKDIR /app/
COPY src .
RUN javac -d target `find ./src -name *.java`

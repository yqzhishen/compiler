FROM openjdk:17
WORKDIR /app/
COPY src/*.java ./src/
RUN javac -d target `find ./src -name *.java`

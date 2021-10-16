FROM openjdk:17
WORKDIR /app/
COPY src ./src
COPY path.txt .
RUN javac -d target `cat path.txt`

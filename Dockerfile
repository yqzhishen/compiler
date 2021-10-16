FROM openjdk:17
WORKDIR /app/
COPY src ./src
COPY srclist.txt .
RUN javac -d target `cat srclist.txt`

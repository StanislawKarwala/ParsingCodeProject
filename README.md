# ParsingCodeProject
docker run --rm -it -v C:\Users\stasi\Documents\ParsingCodeProject:/app -w /app openjdk:23-jdk bash

docker build -t parsing-code-app .

mvn clean package -DskipTests
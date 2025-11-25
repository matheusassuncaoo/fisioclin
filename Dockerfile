FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-24-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install 

FROM openjdk:24-jdk


EXPOSE 5060

COPY --from=build /target/fisioclin-1.0.0.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
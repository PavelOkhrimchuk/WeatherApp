
FROM gradle:8.9.0-jdk17 AS build

WORKDIR /app


COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon || return 0


COPY src ./src
RUN gradle build --no-daemon

FROM tomcat:10.1.26-jdk17-temurin-noble


COPY --from=build /app/build/libs/weather-app.war /usr/local/tomcat/webapps/

EXPOSE 8080

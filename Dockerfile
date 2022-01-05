FROM gradle:jdk17 as builder
WORKDIR /usr/src

COPY build.gradle gradle.properties settings.gradle ./
RUN gradle dependencies --no-daemon

COPY ./ ./
RUN gradle clean build bootJar --no-daemon

FROM openjdk:17-jdk-alpine

RUN addgroup -S spring \
 && adduser -S spring -G spring

USER spring:spring
COPY --from=builder /usr/src/build/libs/*.jar ./app.jar

ENTRYPOINT ["java","-jar","/app.jar"]

FROM gradle:jdk17-jammy AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY src /app/src

RUN ./gradlew build --stacktrace -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/shopping-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
EXPOSE 9098
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

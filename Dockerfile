FROM gradle:8.6-jdk21

WORKDIR /app

COPY . .

RUN gradle build

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar --spring.profiles.active=production

EXPOSE 8080

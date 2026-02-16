
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

RUN mv target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
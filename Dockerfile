# Stage 1: Build the JAR using Maven and Java 17
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy maven executable and dependency definitions
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Download dependencies offline
RUN ./mvnw dependency:go-offline -B

# Copy project source and build package
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the built JAR on a lightweight JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
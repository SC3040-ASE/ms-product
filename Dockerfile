# Use an official Maven image to build the project
FROM maven:3.8.3-openjdk-17 AS MAVEN_BUILD

# Copy the pom.xml file and download the dependencies
COPY pom.xml /build/

# Copy the entire project source into the container
COPY src /build/src/

# Set the working directory in the container
WORKDIR /build/

# Leverage on docker caching
RUN mvn dependency:go-offline

# Package the application (skip tests if needed)
RUN mvn package -DskipTests

# Use an official OpenJDK image as the base for running the application
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Expose the port your application is running on
EXPOSE 8080

# Copy the JAR file from the build stage
COPY --from=MAVEN_BUILD /build/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
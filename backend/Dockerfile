# Use the official base image
FROM openjdk:11

# Set the working directory
WORKDIR /usr/src/app

# Copy the Gradle build files and dependencies
COPY build.gradle gradlew settings.gradle ./
COPY gradle gradle

# Copy the source code (including images)
COPY src /usr/src/app/src

# Build the application
RUN ./gradlew build

# Expose the application port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "build/libs/your-app.jar"]
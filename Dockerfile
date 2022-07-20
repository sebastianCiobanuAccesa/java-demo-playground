#Start with a base image containing Java runtime
FROM openjdk:11

#Add the application's jar to the container
COPY target/java-demo-playground-0.0.1-SNAPSHOT.jar /folder/app.jar

#execute the application
ENTRYPOINT ["java", "-jar", "/folder/app.jar"]
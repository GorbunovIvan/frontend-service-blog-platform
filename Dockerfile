FROM openjdk

WORKDIR /app

COPY build/libs/frontend-service-blog-platform-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t ivangorbunovv/frontend-service-blog-platform-image .'
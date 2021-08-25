FROM java:8
WORKDIR /
ADD target/elasticsearch-0.0.1-SNAPSHOT.jar //
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/elasticsearch-0.0.1-SNAPSHOT.jar"]

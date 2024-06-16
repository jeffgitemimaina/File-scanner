FROM openjdk:11
LABEL maintainer="velocisTech.org"
ADD target/filescanner01-0.0.1-SNAPSHOT.jar filescanner01.jar
ENTRYPOINT ["java", "-jar", "filescanner01.jar"]

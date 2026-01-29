FROM amazoncorretto:21
RUN yum install -y curl && yum clean all
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

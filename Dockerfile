FROM openjdk:15.0.2-oracle

ADD blockchain-0.0.1-SNAPSHOT.jar app.jar

ENV TZ Asiz/Shanghai
RUN bash -c 'touch /app.jar'

ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=pro"]

EXPOSE 8181

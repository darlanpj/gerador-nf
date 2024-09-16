FROM openjdk:11-oracle

LABEL mainteiner="darlanpj@gmail.com"

WORKDIR /app
COPY target/geradornotafiscal-0.0.1.jar /app/geradornotafiscal-0.0.1.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","geradornotafiscal-0.0.1.jar"]
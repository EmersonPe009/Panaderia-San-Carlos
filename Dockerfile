
# Etapa 1: compilar el proyecto Maven
FROM maven:3.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: ejecutar la aplicación en Tomcat 9
FROM tomcat:9.0-jdk8-temurin

# Eliminar aplicaciones predeterminadas
RUN rm -rf /usr/local/tomcat/webapps/*

# Publicar nuestro WAR como BACKEND
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/BACKEND.war

# Configurar Tomcat para escuchar el puerto de Render
RUN sed -i 's/port="8080"/port="10000"/' /usr/local/tomcat/conf/server.xml

EXPOSE 10000

CMD ["catalina.sh", "run"]

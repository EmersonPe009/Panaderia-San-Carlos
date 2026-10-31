
# Etapa 1: compilar el proyecto
FROM maven:3.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: ejecutar Tomcat 9
FROM tomcat:9.0-jdk8-temurin

# Eliminar aplicaciones predeterminadas
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar nuestra aplicación
COPY --from=build /app/target/BACKEND.war /usr/local/tomcat/webapps/BACKEND.war

# Cambiar únicamente el puerto HTTP de Tomcat
RUN sed -i 's/Connector port="8080"/Connector port="10000"/' /usr/local/tomcat/conf/server.xml

# Deshabilitar el puerto de apagado de Tomcat
RUN sed -i 's/<Server port="8005"/<Server port="-1"/' /usr/local/tomcat/conf/server.xml

EXPOSE 10000

CMD ["catalina.sh", "run"]


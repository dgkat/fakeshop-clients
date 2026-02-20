FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
COPY web/ssr/build/libs/ssr-all.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /workspace

COPY pom.xml ./
COPY services/sensor_producer/pom.xml services/sensor_producer/pom.xml
COPY services/alert_detector/pom.xml services/alert_detector/pom.xml
COPY services/alert_router/pom.xml services/alert_router/pom.xml
COPY services/plant_monitor_backend/pom.xml services/plant_monitor_backend/pom.xml
COPY services/operator_console_backend/pom.xml services/operator_console_backend/pom.xml
COPY services/action_dispatcher/pom.xml services/action_dispatcher/pom.xml
COPY services/actuator_worker/pom.xml services/actuator_worker/pom.xml
COPY services/maintenance_worker/pom.xml services/maintenance_worker/pom.xml
COPY services/history_service/pom.xml services/history_service/pom.xml
RUN mvn -pl services/action_dispatcher -am dependency:go-offline -B

COPY services/action_dispatcher/src ./services/action_dispatcher/src
RUN mvn -pl services/action_dispatcher -am clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=build --chown=appuser:appgroup /workspace/services/action_dispatcher/target/action-dispatcher-0.0.1-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
USER appuser

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

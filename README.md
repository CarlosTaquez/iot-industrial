# Plataforma de mantenimiento predictivo IoT — base inicial

**Estado:** estructura inicial, no implementación completa. Los ocho proyectos Java compilan por separado, pero aún no contienen lógica de negocio. El Compose inicial levanta **solo Kafka y RabbitMQ**.

## Requisitos
- Docker Desktop con Docker Compose
- Java 17 y Maven para ejecución local de microservicios

## Arranque
```powershell
Copy-Item .env.example .env
docker compose up -d
docker compose ps
```

RabbitMQ Management: http://localhost:15672 (credenciales en `.env`). Kafka: localhost:9092 desde Windows; kafka:19092 desde otros contenedores. RabbitMQ: localhost:5672 desde Windows; rabbitmq:5672 desde otros contenedores.

## Crear los topics (después de arrancar Kafka)
```powershell
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic sensor_data --partitions 3 --replication-factor 1
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic sensor_status --partitions 3 --replication-factor 1
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic alerts_critical --partitions 3 --replication-factor 1
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic alerts_warning --partitions 3 --replication-factor 1
```

## Ejecutar un microservicio local (ejemplo)
```powershell
cd services/sensor_producer
mvn spring-boot:run
```

## Contratos
Ejemplos en `contracts/examples/`. `sensor_id` debe ser la **key** de Kafka para conservar orden por sensor dentro de una partición.
- `sensor_data`: lecturas de vibración.
- `sensor_status`: estado NORMAL/WARNING/CRITICAL; el productor/detector deberá publicarlo según el diseño acordado.
- `alerts_critical` y `alerts_warning`: mensajes con `alert_id` único.
- RabbitMQ fanout `human_alerts`: mensaje `human_alert.json`.
- `POST /decide` en action_dispatcher, puerto 8083: `decision.json`.
- RabbitMQ colas `critical_actions_queue`, `maintenance_queue`: `command.json`.
- WebSockets previstos: puerto 8081 (monitoreo) y 8082 (consola); rutas por definir.

## Acciones
- `APAGADO_INMEDIATO`: cola crítica.
- `PROGRAMAR_MANTENIMIENTO_AHORA`: cola de mantenimiento.
- `RECONOCER_Y_ESPERAR_24H`: mensaje diferido 24 horas hacia mantenimiento.
- `IGNORAR_10_MINUTOS`: definir comportamiento y expiración/supresión de alertas antes de implementar.

## Pendientes esenciales
1. Implementar los 8 servicios y dashboard.
2. Configurar exchanges, bindings y colas RabbitMQ.
3. **Instalar y habilitar** el plugin `rabbitmq_delayed_message_exchange` en una imagen compatible. **No está habilitado en esta base**.
4. Integrar los Dockerfiles en Compose con healthchecks y variables para red interna.
5. Validar `alert_id` y opciones permitidas en el dispatcher; evitar decisiones repetidas.
6. Pruebas de ventanas móviles y extremo a extremo.

**Seguridad:** credenciales solo para desarrollo local. No publicar `.env`.

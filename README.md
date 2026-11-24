# Plataforma de mantenimiento predictivo IoT

Proyecto académico local para monitoreo industrial y mantenimiento predictivo. La solución se ejecuta con Docker Compose y está formada por microservicios Java 17/Spring Boot, Kafka, RabbitMQ y PostgreSQL.

## Estructura del codigo Java

Los microservicios organizan sus clases por responsabilidad dentro de su paquete base: `api` contiene controladores y manejo HTTP; `application` coordina casos de uso; `domain` contiene reglas y modelos de negocio; `dto` define los contratos HTTP; `persistence` agrupa entidades y repositorios; `messaging` contiene productores, consumidores y eventos; `config` guarda configuraciones de Spring. Los servicios con WebSocket agrupan sus handlers en `websocket`. La clase `*Application` permanece en el paquete base para que Spring detecte los componentes de los subpaquetes.

## Inicio local

Requisitos: Docker Desktop con Docker Compose. Las imagenes de los servicios Java se compilan desde sus Dockerfiles.

```powershell
Copy-Item .env.example .env
docker compose up -d --build
```

El unico archivo `docker-compose.yml` configura Kafka, RabbitMQ, PostgreSQL y todos los microservicios. Compose publica Kafka en `localhost:9092`, RabbitMQ en `localhost:5672` y su consola en `localhost:15672`. PostgreSQL usa el puerto `5432`; los backends usan `8081`, `8082`, `8083` y `8090`.

## Integración continua

GitHub Actions compila los nueve módulos y ejecuta sus pruebas al hacer `push` o abrir/actualizar un pull request. Para ejecutar la misma validación localmente, usa `mvn --batch-mode --no-transfer-progress verify` desde la raíz.
## Modelo de dominio

El detector mantiene un agregado `SensorVibrationWindow` por sensor, que valida lecturas, conserva una ventana móvil de tres valores y gobierna las transiciones de criticidad. `CriticityStrategy` separa la política de evaluación de la infraestructura Kafka; `ThresholdCriticityStrategy` aplica las reglas del ejercicio: crítica por encima de 90 y advertencia con tres lecturas consecutivas por encima de 75.

`IndustrialAlert` valida severidad y acciones y protege su transición de pendiente a resuelta/ignorada. `MaintenanceDecision` valida la elección del operador y limita su transición a despachada. El productor publica `VibrationReadingRecord`, un contrato inmutable con validación fail-fast.

El dispatcher consulta la alerta en PostgreSQL, confirma que pertenece al sensor, permite solo acciones válidas para su severidad y registra la resolución, la auditoría y el mensaje de outbox dentro de una transacción. El relay publica el mensaje, espera confirmación del broker y reintenta si falla. Los workers guardan recibos por `decision_id` para ignorar redeliveries. `alert_resolutions` impide aceptar otra decisión para esa alerta. Para una base ya creada, aplica una vez la migración:

```powershell
docker compose exec -T postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f /migrations/001_decision_id.sql'
```

Una base vacía recibe el esquema actualizado desde `database/init.sql`.

RabbitMQ se construye desde `infra/rabbitmq/Dockerfile` con el plugin Delayed Message Exchange. `RECONOCER_Y_ESPERAR_24H` envía un mensaje con `x-delay` de 24 horas. El plugin exige compatibilidad entre su versión y RabbitMQ; aquí se fija la versión 3.13.0 con RabbitMQ 3.13.

## Flujo principal

1. `sensor_producer` publica lecturas en `sensor_data`, usando el identificador del sensor como clave Kafka.
2. `alert_detector` calcula estado y alertas y publica en `sensor_status`, `alerts_critical` y `alerts_warning`.
3. `alert_router` remite alertas a RabbitMQ para la consola del operador.
4. `action_dispatcher` recibe decisiones y las enruta a los workers de actuador o mantenimiento.
5. `history_service` persiste telemetría, alertas y decisiones en PostgreSQL.

## Temas Kafka

Crear los temas tras iniciar Kafka:

```powershell
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic sensor_data --partitions 3 --replication-factor 1
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic sensor_status --partitions 3 --replication-factor 1
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic alerts_critical --partitions 3 --replication-factor 1
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic alerts_warning --partitions 3 --replication-factor 1
```

Las credenciales de `.env` son solo para desarrollo local. No subas ese archivo al repositorio.

## Pruebas unitarias

Ejecutar desde cada módulo que tiene pruebas de dominio:

```powershell
mvn -f services/sensor_producer/pom.xml test
mvn -f services/alert_detector/pom.xml test
mvn -f services/alert_router/pom.xml test
mvn -f services/action_dispatcher/pom.xml test
```

Cada microservicio tiene su propio `pom.xml`, que hereda la configuración común del POM raíz. Docker Compose construye cada imagen Java desde el contexto raíz y selecciona el módulo correspondiente.

# Despliegue IoT Industrial en AWS

## Estado del despliegue

El entorno de demostración en AWS se encuentra desplegado y sus flujos funcionales se han probado en la instancia existente. Esto describe el estado operativo reportado; no demuestra que el entorno completo pueda reconstruirse desde cero usando solo este repositorio. La restauración de RabbitMQ en un entorno limpio sigue pendiente.

## Arquitectura

- Región: `us-east-2` (Ohio).
- Cómputo: instancia EC2 con Amazon Linux 2023.
- Administración: AWS Systems Manager Session Manager; no se requiere SSH ni reglas de entrada en el Security Group.
- Contenedores: 12 en total: Kafka, RabbitMQ, PostgreSQL y nueve microservicios Java.
- Microservicios: `sensor_producer`, `alert_detector`, `alert_router`, `action_dispatcher`, `actuator_worker`, `maintenance_worker`, `operator_console_backend`, `plant_monitor_backend` e `history_service`.

Los actuadores son workers simulados del proyecto; esta validación no acredita la conexión a actuadores físicos.

## Archivos Docker Compose de AWS

La configuración de AWS combina estos seis archivos, en este orden:

1. `docker-compose.yml` — Kafka 3.9.1 y RabbitMQ 3.13.
2. `docker-compose.postgres.yml` — PostgreSQL 16.
3. `docker-compose.aws.yml` — ajustes de red para los servicios de infraestructura.
4. `docker-compose.services.aws.yml` — ocho microservicios.
5. `docker-compose.history.aws.yml` — `history_service`.
6. `docker-compose.dashboard.aws.yml` — publicación local de los puertos de los backends.

`docker-compose.history.yml` y `docker-compose.services.yml` son variantes locales y no forman parte de esta combinación AWS. El overlay AWS usa `!override`; se requiere Docker Compose 2.24.4 o posterior. Compose combina los archivos en el orden indicado. [Documentación de combinación de Docker Compose](https://docs.docker.com/reference/compose-file/merge/)

## Variables de entorno

Crear `.env` local a partir de `.env.example` y definir los nombres requeridos por los Compose:

| Variable | Uso |
| --- | --- |
| `POSTGRES_DB` | Base de datos de la aplicación |
| `POSTGRES_USER` | Usuario de PostgreSQL |
| `POSTGRES_PASSWORD` | Contraseña de PostgreSQL |
| `RABBITMQ_USER` | Usuario de RabbitMQ usado por los servicios |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ |

Los archivos AWS usan estas variables para configurar los servicios. No guardar valores reales en Git, documentación, capturas ni salida de comandos. No imprimir la configuración Compose expandida: puede contener secretos. Los volúmenes Docker persisten datos entre recreaciones de contenedores en el mismo host, pero no reemplazan respaldos y no garantizan recuperación si se elimina el host o el volumen.

## Validar y arrancar

Ejecuta los comandos desde la raíz del repositorio en la instancia. La validación completa de Compose es:

```powershell
docker compose --env-file .env -f docker-compose.yml -f docker-compose.postgres.yml -f docker-compose.aws.yml -f docker-compose.services.aws.yml -f docker-compose.history.aws.yml -f docker-compose.dashboard.aws.yml config --quiet
```

`config --quiet` valida sin imprimir el modelo expandido. Este comando requiere que `.env` esté presente; no se incluye ni se muestra aquí. [Referencia de `docker compose config`](https://docs.docker.com/reference/cli/docker/compose/config/)

Arranca primero la infraestructura base:

```powershell
$Base = @("--env-file", ".env", "-f", "docker-compose.yml", "-f", "docker-compose.postgres.yml", "-f", "docker-compose.aws.yml")
docker compose @Base up -d
```

Antes de crear topics, consulta los existentes y compara la salida con los nombres del código. No elimines topics que ya existan en AWS solo porque no aparezcan en el código revisado:

```powershell
docker compose @Base exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --list
```

El código fuente actualmente identifica cuatro topics Kafka: `sensor_data`, `sensor_status`, `alerts_critical` y `alerts_warning`. Crea solo los que no existan:

```powershell
$Topics = @("sensor_data", "sensor_status", "alerts_critical", "alerts_warning")
$Existing = docker compose @Base exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --list
foreach ($Topic in $Topics) {
    if ($Topic -notin $Existing) {
        docker compose @Base exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --create --if-not-exists --topic $Topic --partitions 3 --replication-factor 1
    }
}
```

Los microservicios Java revisados utilizan estos cuatro topics Kafka: `sensor_data`, `sensor_status`, `alerts_critical` y `alerts_warning`. Puede existir un quinto topic creado previamente en AWS, pero no se encontró una dependencia de ese topic en el código Java revisado. `operator_decisions` no es un topic Kafka: es un exchange de RabbitMQ y también el nombre de una tabla PostgreSQL.

La topología RabbitMQ de `docs/aws/rabbitmq/topology.json` describe dos exchanges, cuatro colas y cinco bindings. `docs/aws/rabbitmq/restore.sh` intenta importarla, pero su nombre de contenedor está fijado manualmente y no se ha validado en un entorno limpio. Verifica el contenedor generado por Compose y el procedimiento de importación antes de depender del script; la restauración limpia sigue siendo una limitación conocida.

Cuando la infraestructura y la topología estén listas, construye e inicia los nueve microservicios y los servicios de dashboard:

```powershell
$All = @("--env-file", ".env", "-f", "docker-compose.yml", "-f", "docker-compose.postgres.yml", "-f", "docker-compose.aws.yml", "-f", "docker-compose.services.aws.yml", "-f", "docker-compose.history.aws.yml", "-f", "docker-compose.dashboard.aws.yml")
docker compose @All up -d --build
docker compose @All ps
```

No se debe interpretar `ps` como una prueba de salud funcional por sí sola; valida también logs, persistencia, topics y flujos de aplicación.

## Acceso al dashboard por túneles SSM

Los Compose AWS enlazan los puertos publicados a `127.0.0.1` en la instancia. Para acceder desde Windows, abre una sesión de reenvío por cada puerto en una terminal separada y usa el puerto local correspondiente:

```powershell
aws ssm start-session --profile terraform-iot --region us-east-2 --target i-028f946fc7181a43e --document-name AWS-StartPortForwardingSession --parameters '{"portNumber":["8081"],"localPortNumber":["18081"]}'
```

Repite el comando para los pares remoto/local `8082/18082`, `8083/18083` y `8090/18090`. Luego abre `http://localhost:18081`, `http://localhost:18082`, `http://localhost:18083` y `http://localhost:18090`.

AWS requiere autorización IAM tanto para el documento de sesión solicitado como para el destino; `AWS-StartPortForwardingSession` es distinto de `SSM-SessionManagerRunShell`. La política local `iot-industrial-demo-ssm-access-v3.json` limita `StartSession` al documento de shell predeterminado y, por sí sola, no autoriza estos túneles. Confirma qué permiso separado y aprobado habilita el reenvío antes de depender de esta política. [AWS: iniciar una sesión especificando un documento](https://docs.aws.amazon.com/systems-manager/latest/userguide/getting-started-specify-session-document.html)

## Validaciones funcionales reportadas

- Se ejecutaron los 12 contenedores de la demostración.
- Se verificó el flujo de datos de sensores simulados y la generación/distribución de alertas.
- Se comprobó almacenamiento de lecturas, alertas y decisiones en PostgreSQL.
- Se procesaron siete decisiones del operador.
- Se probaron los flujos de `actuator_worker` y `maintenance_worker`; representan acciones simuladas, no hardware industrial real.
- Se accedió a los backends mediante túneles SSM según el despliegue reportado.

Estos resultados corresponden al entorno existente. No se afirma que todos los pasos de una instalación limpia estén automatizados o repetidos de forma independiente.

## Seguridad y costos

- No hay puertos entrantes del Security Group para SSH, RabbitMQ, Kafka, PostgreSQL o los backends. Los puertos de infraestructura y dashboard publicados por los overlays AWS se enlazan a `127.0.0.1`.
- El acceso administrativo usa Session Manager y depende de la política IAM del operador, el rol SSM de la instancia, el agente y la conectividad HTTPS saliente.
- Mantén `.env` local y protegido; nunca lo incluyas en commits ni en salidas compartidas.
- EC2 genera cargos mientras está encendida; EBS puede seguir generando cargos mientras el volumen existe. La IPv4 pública automática se libera al detener la instancia y normalmente se asigna otra al reiniciar; la transferencia de datos y los respaldos también pueden tener costo. Confirma las tarifas y créditos vigentes en Billing antes de usarla. [Ciclo de vida de IPv4 pública en EC2](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-instance-addressing.html)
- Los volúmenes Docker locales no sustituyen un respaldo externo. Protege PostgreSQL, Kafka y la definición/topología RabbitMQ antes de eliminar la instancia o los volúmenes.

## Limitaciones pendientes

1. Confirmar si AWS conserva un topic adicional creado previamente y documentar su origen si resulta relevante; no se halló una dependencia Java revisada.
2. Validar la restauración de RabbitMQ desde cero y eliminar la dependencia de un nombre de contenedor supuesto en el procedimiento operativo futuro.
3. Documentar y repetir de forma controlada todos los pasos de instalación, variables, topología, arranque y verificación sobre un host limpio.
4. Confirmar que la identidad/política autorizada permite `AWS-StartPortForwardingSession` para los túneles del dashboard.
5. Definir respaldos y recuperación de datos y revisar Billing/costos del periodo de demostración.

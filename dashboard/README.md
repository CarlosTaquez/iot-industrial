# FORGEOPS · panel de operaciones

Interfaz de supervisión en español para la plataforma IoT Industrial. La vista principal consume los WebSockets de planta y operador y las API de historial, decisiones y simulación.

## Ejecución local

Desde la raíz del repositorio:

```bash
docker compose up -d --build
python -m http.server 5500 --directory dashboard
```

Abre <http://localhost:5500>. El productor expone `POST http://localhost:8084/simulate`. El botón publica una lectura en Kafka y la cadena normal de consumidores la procesa; no inserta datos directamente en la interfaz.

La simulación automática conserva una condición por sensor (normal, advertencia o crítica). Los cambios son poco frecuentes y las lecturas permanecen en el rango de esa condición, para representar desviaciones sostenidas en lugar de picos aleatorios aislados. El estado vive en memoria y vuelve a normal al reiniciar `sensor_producer`.

## Vistas

- **Vista general:** indicadores, mapa en vivo, alertas accionables, tendencias y actividad reciente.
- **Activos:** estado actual de los diez sensores.
- **Centro de alertas:** alertas pendientes.
- **Historial:** alertas y decisiones registradas por `history_service`.
- **Arquitectura:** resumen de servicios y comprobación de API.
- **Configuración:** umbrales y detalles informativos del panel.

Las alertas se cargan desde el historial y los eventos nuevos llegan por WebSocket. Las acciones disponibles se leen del mensaje de alerta; una decisión se envía a `POST /decide` y la alerta se retira de pendientes al recibir aceptación. Exportar reporte descarga un CSV con las alertas recibidas durante la sesión de la página, junto con estado y acción registrada.

## Métricas de sesión

**Disponibilidad** es una estimación del porcentaje de lecturas recibidas en esta sesión cuyo estado fue `NORMAL` sobre el total de lecturas que procesó la página. No representa disponibilidad histórica del equipo ni tiempo de operación certificado.

**MTBF de sesión** es el promedio de tiempo entre alertas recibidas durante la sesión actual. Se muestra en minutos u horas y aparece como `—` hasta que llega la segunda alerta. Es una referencia de esta sesión, no un MTBF de mantenimiento calculado sobre una ventana histórica.

La gráfica muestra las últimas lecturas recibidas por sensor en el navegador. Una recarga inicia una sesión de interfaz nueva; el historial guardado se usa para poblar el estado inicial de sensores y las alertas pendientes, pero no se mezcla con las métricas de sesión.

## Servicios y puertos

| Servicio | Uso desde el panel |
| --- | --- |
| `plant_monitor_backend` · 8081 | Estado de sensores y alertas por WebSocket |
| `operator_console_backend` · 8082 | Alertas operables por WebSocket |
| `action_dispatcher` · 8083 | Registro de decisiones con `POST /decide` |
| `sensor_producer` · 8084 | Simulación manual con `POST /simulate` |
| `history_service` · 8090 | Lecturas, alertas y decisiones del historial |

El perfil del operador es informativo: el proyecto no configura autenticación de usuarios. Si el dashboard se publica en otro host, actualiza las URL de servicio definidas al inicio del script en `index.html`.

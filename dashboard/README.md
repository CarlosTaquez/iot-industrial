# Panel interactivo

El panel estático está en `index.html`. Con Docker Compose levantado, sirve el directorio desde la raíz del proyecto:

```bash
python -m http.server 5500 --directory dashboard
```

Abre <http://localhost:5500>. El panel se conecta a los WebSockets de planta y operador y consulta el historial y el dispatcher en `localhost`.

Incluye tendencias locales de las últimas lecturas, indicador de conexión y retraso, filtros de alertas, detalle de reglas, asociación local de sensores con equipos y ubicación, confirmación para el apagado inmediato, mensajes de resultado y una vista adaptable a móvil. Los nombres de equipo se guardan en el almacenamiento local del navegador; no se comparten entre usuarios.

Las decisiones se muestran como **despachadas** cuando el sistema las acepta y las envía al flujo de trabajo. El proyecto no tiene inicio de sesión de operadores ni confirma la ejecución física del mantenimiento.

Para detener el servidor, pulsa `Ctrl+C` en la terminal donde lo iniciaste. Los servicios de Docker pueden seguir ejecutándose.

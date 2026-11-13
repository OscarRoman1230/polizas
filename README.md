# API de Gestión de Pólizas

API REST (Spring Boot 3, Java 17) para gestionar pólizas de arrendamiento (Individual y Colectiva) y sus riesgos.

## Requisitos
- Java 17+
- Maven 3.9+

## Ejecutar
```bash
mvn clean test
mvn spring-boot:run
```
La API queda en `http://localhost:8080`. Base de datos H2 en memoria con datos de ejemplo (pólizas 1 a 4).

## Seguridad
Todas las peticiones requieren el header `x-api-key: 123456`; sin él responde `401`.

## Endpoints
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/polizas?tipo=&estado=` | Lista pólizas (filtros opcionales: `INDIVIDUAL`/`COLECTIVA`, `VIGENTE`/`RENOVADA`/`CANCELADA`) |
| GET | `/polizas/{id}/riesgos` | Riesgos de una póliza |
| POST | `/polizas/{id}/renovar` | Incrementa canon y prima por IPC, extiende la vigencia y pasa a `RENOVADA` |
| POST | `/polizas/{id}/cancelar` | Cancela la póliza y todos sus riesgos |
| POST | `/polizas/{id}/riesgos` | Agrega un riesgo (solo colectivas) |
| POST | `/riesgos/{id}/cancelar` | Cancela un riesgo |
| POST | `/core-mock/evento` | Mock del CORE: registra el evento en logs |

## Ejemplos
```bash
curl -H "x-api-key: 123456" "http://localhost:8080/polizas?tipo=COLECTIVA&estado=VIGENTE"

curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/1/renovar

curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
  -d '{"direccionInmueble":"Calle 50 #10-20","arrendatario":"Pedro Díaz"}' \
  http://localhost:8080/polizas/2/riesgos

curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
  -d '{"evento":"ACTUALIZACION","polizaId":555}' http://localhost:8080/core-mock/evento
```

## Reglas de negocio
- Póliza individual: máximo 1 riesgo. Agregar riesgos solo aplica a colectivas (422 en caso contrario).
- No se renueva una póliza cancelada (409).
- Cancelar una póliza cancela todos sus riesgos.
- Renovación: `canon = canon × (1 + IPC)`, `prima = canon × meses`, nueva vigencia por el mismo periodo (IPC en `app.renovacion.ipc`).
- Códigos: 400 validación, 401 sin API key, 404 no encontrado, 409 conflicto de estado, 422 regla de negocio.

## Integración con el CORE
Cada operación que modifica estado publica un evento de dominio; **tras el commit** de la transacción, un listener lo envía mediante el puerto `CoreGateway` (adapter `HttpCoreGateway`) a `/core-mock/evento`, que lo registra en logs. Un fallo del CORE no revierte la operación local.

## Estructura
`controller` (HTTP) → `service` (casos de uso) → `repository` (persistencia). Las reglas de negocio viven en el dominio (`Poliza`, `Riesgo`). `integration` aísla el CORE.

## Decisiones y supuestos
- En producción: patrón Outbox + reintentos + circuit breaker hacia el servicio de edición en WebLogic.
- El IPC es un parámetro configurable (en producción, tabla por año).
- H2 en memoria por simplicidad; el código es portable a Oracle/PostgreSQL.
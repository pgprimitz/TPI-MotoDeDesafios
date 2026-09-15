# Eventos del Motor

Todo sale por Kafka en el sobre común de la plataforma. Un hecho, un evento: el mismo lo consumen varios.

![Eventos](diagramas/eventos.png)

## Sobre

```json
{
  "eventId": "123e4567-e89b-12d3-a456-426614174001",
  "eventType": "INTENTO_FINALIZADO",
  "timestamp": "2026-09-05T20:00:00Z",
  "producer": "tema-03-motor",
  "payload": {
    "intentoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "numeroVersion": 2,
    "dificultad": "MEDIUM",
    "alumnoId": "9b2d8f01-3c4a-4b5e-9f01-7a2b3c4d5e6f",
    "cursoId": "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d",
    "obligatorio": true,
    "estado": "CERRADO",
    "aprobado": true,
    "puntaje": 80,
    "scoreIA": 65,
    "tiempoSegundos": 1500,
    "puntualidad": "A_TIEMPO"
  }
}
```

- eventId: UUID nuevo por emisión. Si un hecho se republica, el eventId cambia: los consumidores aplican por intentoId y vale lo último.
- eventType: UPPER_SNAKE, fijo por hecho.
- timestamp: UTC, momento de emisión.
- producer: siempre tema-03-motor.
- payload: solo datos del hecho, sin metadatos.

## INTENTO_FINALIZADO

Sale al cerrar con corrección y al vencer sin entrega. Mismo tipo para los dos: el campo estado los distingue (`CERRADO` o `VENCIDO`). Si el score de IA llega después, se republica con el score cargado y mismo intentoId.

Consumen: Roadmap (XP, vidas y cuenta de reintentos), Banco (monedas si aprobó) y Notificaciones, que deriva nota disponible de acá.

## DESAFIO_PUBLICADO

Sale al publicar, con desafioId, título y tipo. Lo consume Notificaciones, que notifica. Roadmap no lo escucha: hace GET cuando necesita los publicados.

## SCORE_IA_CALCULADO (entrada al Motor)

Este es un evento recibido desde T07, no uno publicado por el Motor; por eso la regla `producer: tema-03-motor` del sobre anterior aplica a las salidas de T03. T07 puede entregar el score de uso de IA después del cierre. El evento debe llevar, como mínimo, `intentoId`, `scoreIA` y `timestamp`; `rubricVersion` y la justificación quedan pendientes de acordar con T07. El Motor actualiza el intento y republica `INTENTO_FINALIZADO` con el mismo `intentoId`, para que Roadmap y los demás consumidores apliquen el dato más reciente de forma idempotente.

El nombre definitivo del topic, el productor y la política de reintentos de esta entrada todavía no están cerrados.

## Lo que no existe

Sin evento de agotados (Roadmap lo deduce), sin evento propio de nota disponible (Notificaciones la deriva) y sin eventos a medida por consumidor. Detalle en `adr/0002-evento-por-intento.md`.

## Idempotencia

Publicamos al menos una vez: el evento se guarda en outbox en la misma transacción que cierra el intento y un relay lo envía cuando el bus responde. Detalle en `adr/0008-outbox-idempotencia.md`.

Del lado que consume, la clave es intentoId y vale lo último por timestamp: una republicación trae eventId nuevo con el mismo intentoId. Duplicado exacto se descarta, dato más nuevo pisa.

Del lado que recibe: cerrar es idempotente por estados (un intento cerrado que recibe otra corrección igual confirma sin republicar) y abrir trae clave del llamante (clave repetida devuelve el intento existente en vez de duplicar).

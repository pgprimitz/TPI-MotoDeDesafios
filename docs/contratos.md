# Contratos de integración del Motor de desafíos

Este documento reúne los contratos que el Motor necesita ofrecer y consumir para integrarse con los demás grupos. Es la especificación funcional de integración, no un `openapi.yaml`: los nombres de ruta y algunos topics aún deben confirmarse con sus dueños. Los recorridos visuales están en [`flujos.html`](flujos.html) y la versión narrativa en [`flujos.md`](flujos.md).

## 1. Criterio y fuentes

Para resolver contradicciones entre materiales se aplica este orden:

1. ADRs y documentación vigente del Motor (`docs/adr/`, `docs/modelo-*.md`, `docs/eventos.md` y `docs/flujos.md`).
2. Reglas transversales de la propuesta de arquitectura.
3. PRD y PDFs de relevamiento como fuente funcional y de contexto.

La propuesta anterior de límites del Motor contiene dos decisiones que no se trasladan literalmente al contrato vigente: no existe un evento separado de agotamiento y Notificaciones deriva la nota disponible de `INTENTO_FINALIZADO`. Los puntos todavía ambiguos del PRD o de los PDFs aparecen como **pendientes de acuerdo**, no como campos inventados.

## 2. Propiedad de los datos

| Dato | Dueño | Tratamiento del Motor |
|---|---|---|
| Metadatos del desafío | Motor (T03) | Persiste y expone |
| Versiones del desafío | Motor (T03) | Persiste como fotos inmutables |
| Asignación al curso, orden y desbloqueo | Roadmap/Cursos (T10/T02) | Consulta elegibilidad; no guarda asignación |
| Pertenencia del alumno al curso | Cursos/Matrícula (T02) | No la valida directamente; llega resuelta por Roadmap |
| Preguntas y respuestas teóricas | Teóricos (T04) | No persiste |
| Consigna, código y casos de prueba | Prácticos (T05) | No persiste |
| Corrección académica | T04 o T05 | Guarda el veredicto recibido junto al intento |
| Score de uso de IA | Evaluador IA (T07) | Guarda y replica el score sin calcularlo |
| XP, progreso, vidas y ranking | Roadmap (T10) | Publica el hecho que los dispara |
| Monedas y ledger | Banco (T08) | Publica contexto y resultado; no acredita |
| Ítems e inventario | Mercado (T09) | No consulta ni persiste |
| Mensajes y destinatarios | Social/Notificaciones (T11) | Publica hechos; no compone mensajes |
| Parámetros globales | Backoffice (T12) | No consulta en el MVP |

## 3. Reglas de transporte

### 3.1 REST

- El API Gateway es la única puerta de entrada.
- Las llamadas sincrónicas entre servicios también pasan por el Gateway; no hay comunicación directa ni acceso a bases ajenas.
- Se usa REST cuando el Motor necesita la respuesta para continuar: elegibilidad, disponibilidad de contenido y corrección.
- Las respuestas deben ser autocontenidas y no exponer entidades de la base del servicio vecino.
- El Gateway valida autenticidad y vigencia del token. El Motor aplica autorización propia sobre catálogo y operaciones; Roadmap decide la elegibilidad académica.

### 3.2 Eventos

- Kafka se usa para avisar hechos ya ocurridos, sin esperar una respuesta.
- Todo evento publicado por T03 usa el sobre común de la plataforma.
- La publicación es al menos una vez mediante outbox. Los consumidores deben tolerar duplicados.
- La clave funcional de un resultado es `intentoId`; una republicación tiene otro `eventId` pero representa el mismo intento.
- La clave de partición recomendada para `INTENTO_FINALIZADO` es `intentoId`, para conservar el orden de actualizaciones de un intento.

### 3.3 Identificadores y fechas

| Campo | Tipo | Regla |
|---|---|---|
| `desafioId` | UUID | Identidad estable del desafío lógico |
| `numeroVersion` | entero positivo | Único dentro de `desafioId`; identifica la foto usada por un intento |
| `intentoId` | UUID | Identidad estable de una apertura; cada reintento crea otro |
| `alumnoId` | UUID | Identidad propagada desde el token/contexto |
| `cursoId` | UUID | Cohorte donde ocurre el intento |
| `ownerId` | UUID | Profesor dueño del desafío; no se acepta desde el cliente si el token lo determina |
| `idempotencyKey` | string | Clave del llamante para no duplicar una apertura |
| Fechas | RFC 3339 / UTC | El Motor usa hora del servidor para `inicio` y `timestamp` |

Los números de versión no se reutilizan. T03 es la autoridad que asigna `numeroVersion`; T04 y T05 nunca generan su propio número de integración. Editar un desafío, incluso para corregir un título o cambiar contenido evaluable, crea una foto nueva. El intento conserva la versión con la que abrió.

## 4. Contratos REST que expone el Motor

Las rutas siguientes son las rutas lógicas que deben acordarse con el Gateway. El nombre exacto puede cambiar sin cambiar la semántica.

### 4.1 Crear un desafío

**Actor:** ADMIN o PROFESOR autenticado  
**Ruta lógica:** `POST /desafios`  
**Resultado:** desafío en `BORRADOR` con su primera versión.

```json
{
  "titulo": "Validación de entradas",
  "descripcion": "Diseñar una solución que valide los datos recibidos.",
  "dificultad": "MEDIUM",
  "tipo": "PRACTICO",
  "reintentosPermitidos": 2,
  "duracionMaximaMin": 45,
  "esRecuperacion": false
}
```

Reglas:

- `ownerId` se obtiene del contexto autenticado cuando el actor es PROFESOR.
- El alumno nunca crea desafíos.
- La obligatoriedad no pertenece al desafío: es una decisión de la asignación de Roadmap.
- `reintentosPermitidos` está entre 0 y 3.
- `duracionMaximaMin` puede ser `null` para indicar que no hay límite.
- La respuesta debe incluir `desafioId`, `numeroVersion`, `estado` y los metadatos persistidos.

### 4.2 Consultar el catálogo

**Rutas lógicas:**

- `GET /desafios`
- `GET /desafios/{desafioId}`
- `GET /desafios/{desafioId}/versiones`

El catálogo debe permitir filtrar por estado y dueño según el rol:

- un PROFESOR consulta y edita sus desafíos;
- ADMIN consulta el catálogo global;
- solo los desafíos publicados pueden ser asignados;
- los desafíos borrados lógicamente no aparecen en el catálogo asignable.

Roadmap necesita al menos el listado de publicados. No recibe un evento de catálogo para armar el roadmap: consulta mediante GET cuando lo necesita.

### 4.3 Crear una revisión para editar un desafío

**Ruta lógica:** `PATCH /desafios/{desafioId}`  
**Resultado:** T03 asigna `numeroVersion = N+1` y crea una revisión en borrador. La versión publicada vigente no cambia todavía.

```json
{
  "titulo": "Validación de entradas y errores",
  "descripcion": "Diseñar una solución que valide los datos recibidos.",
  "dificultad": "MEDIUM",
  "tipo": "PRACTICO",
  "reintentosPermitidos": 2,
  "duracionMaximaMin": 45,
  "versionBase": 2
}
```

`versionBase` permite detectar que otro editor publicó una revisión mientras se completaba el formulario.

Reglas:

- Cada edición crea una revisión nueva, aunque cambie solo un typo.
- T03 asigna el número y conserva la relación con la versión publicada anterior.
- Las versiones publicadas anteriores no se editan ni se borran.
- Los intentos existentes no cambian de versión.
- Un desafío con borrado lógico no se puede editar.
- Si el tipo cambia, la revisión nueva necesita contenido en el corrector correspondiente.
- Dos ediciones simultáneas sobre la misma versión base deben resolverse con control optimista y una respuesta de conflicto.

Respuesta conceptual:

```json
{
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "numeroVersion": 3,
  "estadoVersion": "BORRADOR",
  "versionAnterior": 2
}
```

### 4.4 Cargar el contenido en T04 o T05

Crear el desafío en T03 no crea las preguntas, respuestas, consignas ni casos de prueba. Una vez obtenido `desafioId` y `numeroVersion`, el autor carga el contenido en el servicio que corresponde al tipo:

- `tipo = TEORICO` → T04 crea preguntas, respuestas y reglas de corrección;
- `tipo = PRACTICO` → T05 crea consigna, formato de entrega y casos de prueba.

**Dirección:** cliente → Gateway → T04 o T05  
**Rutas lógicas:** `PUT /contenidos-teoricos/{desafioId}/versiones/{numeroVersion}` o `PUT /contenidos-practicos/{desafioId}/versiones/{numeroVersion}`

Solicitud conceptual:

```json
{
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "numeroVersion": 3,
  "contenido": {}
}
```

El campo `contenido` es propiedad del corrector y su esquema no se define en T03. T04/T05 debe guardar una foto identificada por `(desafioId, numeroVersion)` y hacer idempotente la escritura de esa pareja. T03 no guarda una copia del contenido ni crea una segunda tabla de preguntas o tests.

La carga debe completarse antes de publicar. Si cambia cualquier dato que afecte la evaluación, se debe crear otra revisión en T03; no se modifica contenido de una versión ya publicada.

### 4.5 Publicar una versión

**Ruta lógica:** `POST /desafios/{desafioId}/versiones/{numeroVersion}/publicar`  
**Resultado:** la revisión solicitada se convierte en la versión vigente o se rechaza.

Antes de publicar la versión solicitada, el Motor consulta el servicio dueño del contenido usando exactamente el mismo `desafioId` y `numeroVersion`:

- `tipo = TEORICO` → T04;
- `tipo = PRACTICO` → T05.

La respuesta mínima del corrector es:

```json
{
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "disponible": true,
  "motivo": null
}
```

Sin contenido no se publica. La validación ocurre una vez al publicar, no en cada apertura.

### 4.6 Borrado lógico

**Ruta lógica:** `POST /desafios/{desafioId}/borrar`  
**Resultado:** el desafío queda oculto y marcado como borrado.

No existe `DELETE` físico de desafíos. El borrado:

- impide nuevas asignaciones y ediciones;
- conserva versiones, intentos y resultados académicos;
- no modifica ni invalida intentos ya abiertos.

El nombre HTTP exacto debe acordarse con el Gateway; la semántica de borrado lógico no cambia.

### 4.7 Abrir un intento

**Actor:** ALUMNO  
**Ruta lógica:** `POST /intentos`  
**Header:** `Idempotency-Key: <clave-del-llamante>`

```json
{
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "cursoId": "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"
}
```

El `alumnoId` proviene de la identidad autenticada, no del body. El Motor consulta elegibilidad a Roadmap y solo crea el intento cuando recibe autorización.

Respuesta exitosa:

```json
{
  "intentoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "numeroVersion": 2,
  "cursoId": "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d",
  "obligatorio": true,
  "esRecuperacion": false,
  "estado": "ABIERTO",
  "inicio": "2026-09-05T19:35:00Z",
  "fechaCierre": "2026-09-05T20:20:00Z"
}
```

Reglas:

- Un desafío en borrador o borrado no se abre.
- La elegibilidad de Roadmap es obligatoria; sin ella no se crea un intento.
- `version_actual` se copia en el intento dentro de la misma transacción.
- Una clave repetida devuelve el intento previamente creado.
- Cada reintento es un `intentoId` nuevo y tiene reloj propio.

### 4.8 Consultar un intento

**Ruta lógica:** `GET /intentos/{intentoId}`

La consulta debe devolver, como mínimo, estado, desafío, versión, curso, inicio, entrega, tiempo, puntualidad y resultado disponible. Los campos de corrección y score pueden ser `null` mientras el intento está en `EN_CORRECCION` o mientras el score IA está pendiente.

El acceso debe limitarse al alumno dueño, al profesor habilitado por su curso y a ADMIN, según el contrato de autorización transversal.

### 4.9 Entregar un intento

**Actor:** ALUMNO  
**Ruta lógica:** `POST /intentos/{intentoId}/entrega`

El formato interno de `entrega` pertenece a T04 o T05. El Motor la recibe como contenido del tipo correspondiente y la reenvía al corrector; no interpreta preguntas, código ni casos de prueba.

```json
{
  "entrega": {
    "respuestas": [
      { "itemId": "pregunta-01", "respuesta": "..." }
    ]
  }
}
```

Para una entrega práctica, T05 define el contenido equivalente, por ejemplo código, lenguaje y archivos permitidos. Ese esquema no se fija en el Motor.

Respuesta inmediata del Motor:

```json
{
  "intentoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "estado": "EN_CORRECCION",
  "entrega": "ACEPTADA",
  "tiempoSegundos": 1500,
  "puntualidad": "A_TIEMPO"
}
```

Reglas:

- Solo un intento `ABIERTO` acepta entrega.
- Al aceptar la entrega, el reloj se detiene y la demora de corrección no cuenta.
- El Motor calcula `tiempoSegundos` y `puntualidad` con `inicio`, entrega y fechas recibidas de Roadmap.
- Una entrega posterior al cierre solo se acepta si la política de Roadmap contempla una ventana tardía; el tamaño de esa ventana está pendiente de acuerdo.
- Un intento `CERRADO` o `VENCIDO` es final y no acepta otra entrega.

## 5. Contratos REST que consume el Motor

### 5.1 Elegibilidad de Roadmap

**Dirección:** Motor → Gateway → Roadmap  
**Momento:** antes de crear cada intento.

Solicitud mínima:

```json
{
  "alumnoId": "9b2d8f01-3c4a-4b5e-9f01-7a2b3c4d5e6f",
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "cursoId": "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"
}
```

Respuesta mínima:

```json
{
  "habilitado": true,
  "estadoDesbloqueo": "DESBLOQUEADO",
  "obligatorio": true,
  "fechaApertura": "2026-09-05T18:00:00Z",
  "fechaCierre": "2026-09-05T20:20:00Z",
  "esRecuperacion": false,
  "motivoRechazo": null
}
```

Roadmap decide disponibilidad, desbloqueo, pertenencia, reintentos y vidas. Puede rechazar una apertura por cualquiera de esas reglas. El Motor no necesita conocer ni persistir el saldo de vidas: necesita una decisión de autorización y el contexto para registrar el intento.

Si la apertura se rechaza, la respuesta debe ser estable y legible para que el Motor pueda devolver el motivo sin traducir reglas ajenas.

### 5.2 Disponibilidad de contenido

**Dirección:** Motor → Gateway → T04 o T05  
**Momento:** al publicar o cuando cambia el tipo del desafío.

Solicitud mínima:

```json
{
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "numeroVersion": 3,
  "tipo": "PRACTICO"
}
```

El corrector responde si existe contenido suficiente para publicar exactamente esa versión. El contrato exacto de ruta y los criterios de “contenido cargado” pertenecen a T04/T05.

### 5.3 Corrección de una entrega

**Dirección:** Motor → Gateway → T04 o T05  
**Momento:** después de aceptar una entrega.

Solicitud común:

```json
{
  "intentoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "numeroVersion": 2,
  "alumnoId": "9b2d8f01-3c4a-4b5e-9f01-7a2b3c4d5e6f",
  "cursoId": "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d",
  "entrega": {}
}
```

Respuesta común:

```json
{
  "intentoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "estadoCorreccion": "CORREGIDO",
  "aprobado": true,
  "puntaje": 80
}
```

Requisitos del corrector:

- corregir contra `numeroVersion`, no contra la versión vigente;
- devolver el mismo resultado si recibe de nuevo el mismo `intentoId` y entrega;
- distinguir error técnico, corrección pendiente y corrección terminada;
- no cambiar una corrección ya confirmada sin un contrato explícito de apelación.

T05 puede producir feedback, trazas o errores técnicos. Esos datos no forman parte de la persistencia mínima del Motor; si deben exponerse desde T03, hay que agregarlos formalmente al contrato.

## 6. Contratos de eventos publicados por el Motor

### 6.1 Sobre común

```json
{
  "eventId": "123e4567-e89b-12d3-a456-426614174001",
  "eventType": "INTENTO_FINALIZADO",
  "timestamp": "2026-09-05T20:00:00Z",
  "producer": "tema-03-motor",
  "payload": {}
}
```

| Campo | Regla |
|---|---|
| `eventId` | UUID nuevo por emisión; cambia al republicar |
| `eventType` | Nombre estable en `UPPER_SNAKE_CASE` |
| `timestamp` | Instante UTC de emisión, no de apertura |
| `producer` | `tema-03-motor` en eventos publicados por T03 |
| `payload` | Datos del hecho, sin metadatos de transporte |

Los topics concretos deben confirmarse con T11, que define el contrato transversal del bus. El evento debe tener una versión de esquema para permitir evolución compatible.

### 6.2 `DESAFIO_PUBLICADO`

**Productor:** Motor  
**Consumidor:** Notificaciones  
**Uso:** avisar que un desafío ya puede asignarse.

```json
{
  "desafioId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "titulo": "Validación de entradas",
  "tipo": "PRACTICO"
}
```

Roadmap no consume este evento para armar el roadmap; consulta el catálogo publicado por REST.

### 6.3 `INTENTO_FINALIZADO`

**Productor:** Motor  
**Consumidores:** Roadmap, Banco y Notificaciones  
**Uso:** comunicar un intento cerrado con corrección o vencido sin entrega.

```json
{
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
```

Semántica de campos:

- `estado` es `CERRADO` cuando hay corrección y `VENCIDO` cuando el plazo terminó sin entrega.
- `aprobado` y `puntaje` son obligatorios para `CERRADO`; son `null` para `VENCIDO`.
- `scoreIA` puede ser `null` si el evaluador todavía no respondió.
- `dificultad` viaja porque Roadmap la usa para aplicar reglas de XP; el Motor no calcula el monto.
- `obligatorio` y el contexto de curso vienen de Roadmap y se replican para que Banco y otros consumidores no tengan que consultar después.
- `puntualidad` es una marca del Motor: `A_TIEMPO` o `TARDIA`. Roadmap decide cualquier penalidad.

No existe un evento separado de reintentos agotados, nota disponible ni descuento de vida. Roadmap, Banco y Notificaciones derivan esos efectos de este hecho.

### 6.4 Republicación por score IA diferido

Cuando T07 entrega el score después del cierre:

1. el Motor actualiza `scoreIA` del intento;
2. conserva `intentoId` y todos los demás datos del hecho;
3. publica otra vez `INTENTO_FINALIZADO`;
4. genera un `eventId` y `timestamp` nuevos.

Los consumidores deben aplicar la versión más reciente por `intentoId` y timestamp. El cambio de score no crea un segundo intento.

## 7. Contrato de evento que recibe el Motor

### 7.1 `SCORE_IA_CALCULADO`

**Productor:** Evaluador IA (T07)  
**Consumidor:** Motor  
**Uso:** completar o corregir de forma diferida el score de uso de IA.

Carga mínima propuesta:

```json
{
  "intentoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "scoreIA": 65,
  "rubricVersion": "2026-09-v2"
}
```

El Motor debe:

- rechazar o enviar a revisión un `intentoId` inexistente;
- aceptar el evento aunque el intento esté `CERRADO`, porque ese es el caso diferido;
- no recalcular score ni XP;
- actualizar el intento de manera idempotente;
- republicar `INTENTO_FINALIZADO` para que los consumidores reciban la versión nueva.

Con T07 falta acordar el nombre definitivo, topic, envelope, `rubricVersion`, justificación, identificación de una evaluación corregida y política ante dos scores para el mismo intento.

## 8. Qué necesita cada consumidor

### Roadmap y Progreso (T10)

Consume:

- `GET /desafios?estado=PUBLICADO` para consultar catálogo;
- respuesta de elegibilidad antes de cada apertura;
- `INTENTO_FINALIZADO` para XP, progreso, ranking, vidas, desbloqueos y cuenta de reintentos.

Debe usar `desafioId`, `alumnoId`, `cursoId`, `dificultad`, `obligatorio`, resultado, tiempo, puntualidad y score IA. No debe esperar un evento de agotamiento.

### Banco (T08)

Consume `INTENTO_FINALIZADO`. Cuando `estado = CERRADO` y `aprobado = true`, usa `cursoId`, `desafioId`, `alumnoId` y `obligatorio` para acreditar según sus parámetros y registrar el ledger. No debe usar un monto fijo enviado por el Motor.

### Social y Notificaciones (T11)

Consume `DESAFIO_PUBLICADO` e `INTENTO_FINALIZADO`. Decide destinatarios, plantilla y momento de la notificación. La nota disponible y el vencimiento se derivan del hecho; T03 no publica mensajes ni un evento de nota separado.

### T04 y T05

Necesitan `desafioId`, `numeroVersion`, `intentoId`, entrega y contexto mínimo. Deben conservar la semántica de versión congelada y devolver un resultado idempotente.

### T07

Necesita asociar la evaluación a un `intentoId`. La transcripción de la interacción de IA no es propiedad del Motor y no se persiste en T03 salvo acuerdo específico.

## 9. Errores e idempotencia

La forma exacta del error común debe acordarse a nivel Gateway. La semántica mínima es:

| Situación | Resultado esperado |
|---|---|
| Token ausente o inválido | La plataforma rechaza antes de llegar al Motor |
| Rol sin permiso para la operación | Rechazo de autorización |
| Desafío inexistente o borrado | No se crea ni modifica intento |
| Desafío no publicado | No se puede asignar ni abrir |
| Roadmap no habilita | No se crea intento; se devuelve motivo |
| Clave de apertura repetida | Se devuelve el intento existente |
| Intento no abierto al entregar | Rechazo; no se altera el estado |
| Entrega duplicada | Se devuelve el resultado idempotente o el estado vigente |
| Corrector no disponible | No se finaliza la corrección; timeout, reintento y fallback deben acordarse con T04/T05 |
| Bus no disponible al cerrar | El intento permanece cerrado y el outbox reintenta |
| Evento duplicado | Consumidor descarta o reaplica sin duplicar efecto |

El cierre del intento y el registro del evento en `EVENTO_OUTBOX` ocurren en la misma transacción. El relay publica cuando Kafka está disponible y registra sus reintentos.

Mapeo HTTP sugerido para confirmar con el Gateway:

| Código | Uso |
|---|---|
| `400` | Body o parámetros inválidos |
| `401` | Token ausente, inválido o vencido; normalmente lo resuelve el Gateway |
| `403` | Rol o alcance sin permiso |
| `404` | Desafío o intento inexistente |
| `409` | Conflicto de estado o clave de idempotencia incompatible |
| `422` | Roadmap rechaza la elegibilidad o la regla de negocio no permite la transición |
| `502` / `504` | Dependencia externa no disponible o timeout |

## 10. Estados e invariantes compartidos

### Desafío

```text
BORRADOR -> PUBLICADO -> BORRADO_LOGICO
```

- Publicar exige contenido en T04 o T05.
- Solo `PUBLICADO` puede asignarse.
- `BORRADO_LOGICO` no se edita ni se asigna.
- La historia no se elimina físicamente.

### Intento

```text
ABIERTO -> EN_CORRECCION -> CERRADO
ABIERTO -> VENCIDO
```

- Un intento apunta a una única versión.
- El reloj corre solo en `ABIERTO`.
- `CERRADO` y `VENCIDO` son estados finales.
- La entrega posterior al cierre requiere una ventana de tardía definida por Roadmap.
- La corrección y el score se guardan tal como llegan de sus dueños.

## 11. Integraciones que no deben agregarse al Motor

- Acceso directo a la base de T02, T04, T05, T07, T08, T09, T10 o T11.
- Consulta directa a Sandbox (T06); esa relación la media T05.
- Validación, reserva o consumo de ítems de Mercado.
- Cálculo de XP, monedas, vidas, ranking o penalidades.
- Persistencia de asignaciones, pertenencia, roadmap o saldo.
- Evento de agotamiento, nota disponible o descuento de vida.
- Desafíos personalizados generados por LLM dentro del MVP actual; el PDF del Motor los ubica fuera del primer sprint.

## 12. Acuerdos pendientes

1. Rutas definitivas, nombres de campos y formato de error del Gateway.
2. Endpoint y respuesta final de elegibilidad de Roadmap.
3. Endpoints, timeout y estados de corrección de T04/T05.
4. Topic, envelope y esquema de `SCORE_IA_CALCULADO` con T07.
5. Política ante score IA diferido, doble evaluación y `rubricVersion`.
6. Ventana de entrega tardía y efecto del vencimiento sobre vidas y XP.
7. Confirmar si la encuesta puede bloquear la revelación del resultado y qué grupo expone esa confirmación. La documentación vigente del Motor no agrega esa llamada hasta que exista un dueño acordado.
8. Topics, versión de esquema y clave de partición definitivos con T11.

## 13. Trazabilidad

| Decisión o contrato | Fuente |
|---|---|
| No guardar asignación | [`adr/0001-no-asignacion-storage.md`](adr/0001-no-asignacion-storage.md) |
| Un evento por intento y sin agotados | [`adr/0002-evento-por-intento.md`](adr/0002-evento-por-intento.md) |
| El Motor no calcula recompensas | [`adr/0003-motor-no-calcula.md`](adr/0003-motor-no-calcula.md) |
| Versionado congelado | [`adr/0004-intentos-conservan-version.md`](adr/0004-intentos-conservan-version.md) |
| Reloj detenido al entregar | [`adr/0005-reloj-se-frena-al-entregar.md`](adr/0005-reloj-se-frena-al-entregar.md) |
| Publicación exige contenido | [`adr/0006-publicar-exige-contenido.md`](adr/0006-publicar-exige-contenido.md) |
| Ítems fuera del Motor | [`adr/0007-items-referencia-validada.md`](adr/0007-items-referencia-validada.md) |
| Outbox e idempotencia | [`adr/0008-outbox-idempotencia.md`](adr/0008-outbox-idempotencia.md) |
| Flujos end-to-end | [`flujos.md`](flujos.md) |
| Sobre y eventos actuales | [`eventos.md`](eventos.md) |
| Modelo de dominio y persistencia | [`modelo-dominio.md`](modelo-dominio.md), [`modelo-persistencia.md`](modelo-persistencia.md) |
| Límites iniciales del grupo | `MOToR DE DESAFIO.pdf` |
| Reparto de temas y reglas de comunicación | `TUP_PIV_BE_PROPUESTA_ARQ.pdf` |
| Reglas funcionales de producto | `PRD-Plataforma-Gamificada-TP.pdf` |

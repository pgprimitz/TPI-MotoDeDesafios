# Arquitectura del Motor de desafíos

Microservicio dueño del catálogo de desafíos y del ciclo de vida de los intentos. Spring Boot 4 + Java 21, base propia en Postgres, eventos por Kafka, registro en Eureka, entrada por API Gateway.

Ver también: flujos, eventos, modelo de dominio, modelo de persistencia, `../CONTEXT.md`, `adr/`.

## Visión

El Motor responde dos preguntas y nada más: qué desafíos existen y qué intentos abrió cada alumno. Todo lo demás (corregir, puntuar la IA, dar XP, monedas y vidas) pasa en otros servicios. Esta división existe por un motivo práctico: la economía del juego vive en un solo lugar. Si el Motor calculara XP, cada cambio de parámetros habría que hacerlo en dos servicios y el ranking dejaría de cuadrar.

## Principios

Un dato, un dueño. Las fechas, la obligatoriedad y las vidas las decide Roadmap. El Motor las lee y las replica en sus eventos, nunca las guarda como propias. Así no hay sincronización que mantener ni dos versiones de la verdad.

Sincrónico para decidir, evento para avisar. Si el Motor necesita la respuesta para seguir (¿puede abrir?, ¿cuál es el contenido exacto?, ¿aprobó?), llama por el gateway y espera. Si solo cuenta algo que ya pasó (intento cerrado), publica y sigue. Nadie lo bloquea.

Replicar en el evento, no consultar después. Cada evento de intento cerrado lleva el contexto completo: curso, obligatorio, puntualidad, corrección y score IA. Los consumidores no vuelven a preguntar y el historial queda autocontenido.

## Componentes internos

API REST. Expone catálogo e intentos detrás del gateway. Valida identidad por token y delega las reglas de negocio al núcleo. No habla directo con otros servicios: sale por el gateway como cualquier cliente.

Núcleo de intentos. Abre, entrega, cierra y vence intentos. Antes de abrir exige la decisión de Roadmap y recupera el contenido exacto desde T04/T05; solo entonces inicia el cronómetro y congela la versión. También marca puntualidad con las fechas que trae el contexto de Roadmap.

Publicador de eventos. Emite un evento por intento cerrado más hechos para notificaciones (desafío publicado, nota disponible). No reintenta eternamente: si el bus cae, el intento ya está cerrado en base y el evento sale al recuperarse.

Postgres propia. Tres tablas (desafío, versión, intento) que nadie más toca. Detalle en modelo de persistencia.

## Cómo se integra

| Con quién | Tipo | Qué viaja |
|---|---|---|
| Gateway | REST sincrónico | Única entrada. Comandos y consultas del Motor |
| Roadmap/Cursos | REST vía gateway | Decisión de elegibilidad antes de abrir (desbloqueo, vidas) y contexto a replicar |
| Teórico/Práctico | REST vía gateway | Contenido exacto al abrir; entrega con versión a la ida y corrección a la vuelta |
| Evaluador IA | Eventos | Score de uso de IA atado al intento |
| Banco | Eventos | Resultado aprobado con marca de obligatorio para acreditar monedas |
| Notificaciones | Eventos | Desafío publicado y nota disponible para notificar |

## Decisiones clave

- La asignación no se guarda acá, solo se replica su contexto. Evita desajustes cuando Roadmap edita el roadmap. `adr/0001-no-asignacion-storage.md`
- El Motor recupera el contenido exacto y lo entrega junto con el intento. El frontend no consulta T04/T05. `adr/0009-motor-entrega-contenido-al-abrir.md`
- Un evento por intento cerrado, sin evento de agotados. Roadmap deduce el agotamiento con los resultados. `adr/0002-evento-por-intento.md`
- El Motor no calcula nada, publica datos. La economía se ajusta en un solo lugar. `adr/0003-motor-no-calcula.md`
- Cada intento congela su versión. Editar no rompe lo que está en curso. `adr/0004-intentos-conservan-version.md`
- El reloj se frena al entregar. La demora del corrector no la paga el alumno. `adr/0005-reloj-se-frena-al-entregar.md`

## Fuera de alcance

Contenido de preguntas y casos de prueba, parámetros de economía, ranking, chat, encuestas y desafíos personalizados con LLM. Algunos entran en fases futuras; el glosario marca cuáles.

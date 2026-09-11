# T03 - Challenge Engine

Motor de Desafios de la plataforma educativa gamificada.

Esta etapa incorpora la estructura base del microservicio y la persistencia
PostgreSQL del catalogo de desafios mediante Flyway, JPA y repositorios Spring
Data. La organizacion utiliza capas simples de controller, service, model,
entities, repository y enums. Tambien incluye Docker local, Swagger y el
healthcheck de Actuator.

## Ejecucion local

Requisitos:

- Java 21
- Docker con Compose

Desde el directorio `challenge-engine`:

```bash
docker compose up -d
./mvnw spring-boot:run
```

En Windows puede utilizarse `mvnw.cmd spring-boot:run`.

Las variables disponibles son `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` y
`DB_PASSWORD`. La configuracion local predeterminada coincide con
`docker-compose.yml`.

## Infraestructura disponible

- Healthcheck: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Migracion inicial: `db/migration/V1__create_challenge_catalog.sql`
- Tablas: `challenge` y `challenge_version`

## Verificacion

Los tests de persistencia utilizan PostgreSQL mediante Testcontainers. Si el
daemon de Docker no esta disponible, JUnit omite estos tests de integracion.

```bash
./mvnw clean test
./mvnw clean verify
```

## Estado actual del proyecto

IMPLEMENTADO:

- dominio inicial;
- estructura base del microservicio;
- PostgreSQL y Flyway;
- persistencia de `Challenge`;
- persistencia de `ChallengeVersion`;
- repositorios Spring Data;
- Docker local;
- Swagger/OpenAPI;
- healthcheck de Actuator.

PENDIENTE:

- casos de uso de catalogo;
- versionado funcional;
- publicacion;
- baja logica;
- intentos;
- resultados;
- persistencia Outbox;
- Kafka;
- integraciones con otros microservicios.

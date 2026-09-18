# Challenges

An application for organising and recording racing-simulator challenges. It provides a shared platform for Assetto Corsa, WRC Generations, and similar events: participants compete on a selected car/track combination, register their best lap times, and compare results on a live leaderboard.

## Business rules

- Only one active challenge may exist for a given car and track.
- A participant may register or quit only while a challenge is active.
- Joining multiple active challenges requires a previous challenge win.
- Participants may update only their own lap time while a challenge is active; administrators may update any registered participant, including after it closes.
- Closed challenges cannot be changed or deleted.
- Lap times use `m:ss.S`, `m:ss.SS`, or `m:ss.SSS` input (for example, `1:20.1`) and are returned as `mm:ss.SSS`.

## Roles

Roles are cumulative: a user may be authorized as both `PARTICIPANT` and `ADMIN`, and can then perform actions permitted to either role. `ADMIN` does not implicitly authorize participant actions; a user who needs both sets of actions must be authorized for both roles.

| Authorization role | Represents | Permitted actions |
| --- | --- | --- |
| `PARTICIPANT` | Authorization to act as a racer in challenges. | Register for and quit active challenges; update their own lap time while the challenge is active. |
| `ADMIN` | Authorization to administer challenge reference data and challenges. | Create and update cars and tracks; create, change the end date of, and delete active challenges; update any registered participant's lap time, including after a challenge closes. |

## Prerequisites

- JDK 21
- PostgreSQL with a database named `challenges`

## Run

The `local` profile supplies the local PostgreSQL connection. Update its credentials if necessary in `src/main/resources/application-local.properties`, then run:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

The service starts at `http://localhost:8790/challenges/svc`. Flyway applies the schema and reference-data migrations at startup.

## Tests

```bash
./mvnw test
```

This runs the test suite with H2 and generates the JaCoCo report at `target/site/jacoco/index.html`.

## Architecture and design

The application uses a layered design with explicit dependency boundaries:

- **Controllers** are the HTTP edge: they validate and map requests/responses, then delegate to services. They do not access repositories directly.
- **Services** own application workflows, transactions, authorization-sensitive decisions, and business rules. They do not depend on controllers and are intentionally stateless apart from injected dependencies.
- **Repositories** are Spring Data JPA interfaces for persistence and query projections. They do not depend on services or controllers.
- **Models** represent racing concepts and their persistence relationships. `Challenge` is the central domain concept: it brings a selected car and track together with the participants competing in it and its current result. `Participant` belongs to a challenge and currently identifies the user by name.

ArchUnit tests enforce these boundaries and conventions: controller classes cannot be accessed by other layers; controllers cannot use repositories; services cannot depend on controllers; repositories cannot depend on services or controllers; and controllers, services, and repositories use constructor or setter injection rather than field injection. Utility classes remain non-Spring, state-free helpers without dependencies on the controller or service layers.

This is a pragmatic clean-architecture direction rather than a pure ports-and-adapters implementation: business workflows are kept out of HTTP and persistence adapters, while services still use Spring Data repositories and JPA entities directly.

Participant actions are intentionally split by responsibility. `ChallengeService` owns registration and quitting because those actions change challenge membership and must enforce challenge-level eligibility rules. `ParticipantService` owns lap-time updates because it maintains the participant result and recomputes the challenge leader when necessary.

All persisted racing entities inherit `BaseEntity`, which provides identity, optimistic-locking version, and creation/modification auditing. Cars, tracks, challenges, and participants maintain normalized, denormalized search fields in JPA lifecycle callbacks so case- and diacritic-insensitive searches do not leak persistence concerns into services.

## Custom configuration

- `challenges.request-logging.enabled` enables logging of JSON `POST`, `PUT`, and `PATCH` bodies. It is enabled by the local profile only.
- `challenges.request-logging.exclude-paths` lists Ant-style paths omitted from request-body logging; `/users/register` is excluded to avoid logging credentials.
- `JacksonConfig` defines the API lap-time format: inputs accept `m:ss.S`, `m:ss.SS`, or `m:ss.SSS`, while responses use canonical `mm:ss.SSS`.
- `MessageSource` resolves application business-message codes from `messages.properties`, falling back to the code when no translation exists.

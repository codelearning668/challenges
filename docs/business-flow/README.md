# Challenges business-flow diagrams

This folder contains source-grounded documentation for the application flows reconstructed from the controller, service, security configuration, and controller integration tests.

## Delivered files

- `challenges-business-flows.dot` — editable Graphviz source.
- `challenges-business-flows.png` — high-resolution visual diagram.
- `challenges-business-flows.pdf` — printable PDF.

The diagram has three sections:

1. **Access, browsing, and registration**
   - An unregistered visitor can browse all GET endpoints and can register.
   - Registration rejects an already-used username; successful registration enables the user, BCrypt-encodes the supplied password, and assigns `PARTICIPANT`.
   - A known authenticated participant can enter the role-gated active-operation flows.
   - The security configuration permits all GET requests. `/users/info` derives its response from the current Spring Security context, so it is meaningful for an authenticated caller.

2. **ADMIN flow**
   - An `ADMIN` maintains cars and tracks, then creates a challenge from an existing track and car.
   - Only one active challenge can exist for a particular track/car pair.
   - An admin may change the end date or delete only an active challenge.
   - An admin may correct or clear any *registered* participant’s lap time, including after the challenge has ended. The system updates the current leader accordingly.

3. **PARTICIPANT flow**
   - A `PARTICIPANT` registers for an active challenge only when not already registered.
   - A participant who is competing in another active challenge must have won **any earlier challenge** before registering concurrently.
   - A participant may submit or remove only their own lap time and only while the challenge is active.
   - A participant may quit only an active challenge for which they are registered.

## Business semantics captured

- **Active challenge:** the challenge end date is today or later in UTC. A past end date means closed.
- **Leader:** the registered participant with the quickest non-null lap time. If the leader’s time becomes slower or is cleared, the leader is recalculated; if no times remain, the leader is cleared.
- **Authorization:** roles are cumulative. The diagram does not assume `ADMIN` automatically includes `PARTICIPANT`; endpoints are guarded by their explicit role declarations.

## Source basis

- `src/main/java/sk/mkrajcovic/challenges/controller/*Controller.java`
- `src/main/java/sk/mkrajcovic/challenges/service/{ChallengeService,ParticipantService,UserService,CarService,TrackService,SystemService}.java`
- `src/main/java/sk/mkrajcovic/challenges/config/SecurityConfig.java`
- `src/main/java/sk/mkrajcovic/challenges/security/UserRoles.java`
- Controller integration tests under `src/test/java/sk/mkrajcovic/challenges/controller/`

## Regeneration

From the project root:

```bash
dot -Tpng -Gdpi=180 docs/business-flow/challenges-business-flows.dot -o docs/business-flow/challenges-business-flows.png
dot -Tpdf docs/business-flow/challenges-business-flows.dot -o docs/business-flow/challenges-business-flows.pdf
```

# AGENTS

This repository is a small Dropwizard 4 service for managing wizard registrations. Start with the API and runtime overview in [README.md](README.md); use this file for repo-specific execution guidance that is easy to miss from a quick code scan.

## Working Agreement

- Keep changes inside the existing layering: `resources -> service -> db -> core/api`.
- Prefer editing source under `src/main` and tests under `src/test`; do not hand-edit `target/`.
- Treat `dependency-reduced-pom.xml` as build output from the shade plugin unless the task is explicitly about packaging.
- Wire new runtime components in `WizardRegistryApplication`; there is no DI container.

## Build And Validation

- Full validation: `mvn clean verify`
- Unit tests only: `mvn surefire:test`
- Integration tests only: `mvn failsafe:integration-test`
- Build runnable jar without tests: `mvn clean package -DskipTests`
- Run locally: `java -jar target/wizard-registry-1.0-SNAPSHOT.jar server src/main/resources/wizard-registry.yml`

Use the narrowest validation that matches the slice you changed. For behavior changes in HTTP endpoints, prefer the integration test style in [src/test/java/org/ministry/magic/WizardRegistryApplicationIT.java](src/test/java/org/ministry/magic/WizardRegistryApplicationIT.java). For service-only logic, mirror [src/test/java/org/ministry/magic/service/WizardServiceTest.java](src/test/java/org/ministry/magic/service/WizardServiceTest.java).

## Code Map

- App bootstrap and all runtime wiring: [src/main/java/org/ministry/magic/WizardRegistryApplication.java](src/main/java/org/ministry/magic/WizardRegistryApplication.java)
- Configuration and YAML-backed settings: [src/main/java/org/ministry/magic/WizardRegistryConfiguration.java](src/main/java/org/ministry/magic/WizardRegistryConfiguration.java), [src/main/resources/wizard-registry.yml](src/main/resources/wizard-registry.yml)
- REST resources: [src/main/java/org/ministry/magic/resources/](src/main/java/org/ministry/magic/resources/)
- Business rules and status transitions: [src/main/java/org/ministry/magic/service/WizardService.java](src/main/java/org/ministry/magic/service/WizardService.java)
- Persistence and SQL/JDBI bindings: [src/main/java/org/ministry/magic/db/](src/main/java/org/ministry/magic/db/)
- Domain types and enums: [src/main/java/org/ministry/magic/core/](src/main/java/org/ministry/magic/core/)
- Lifecycle setup, seed data, health, and servlet/filter integrations: [src/main/java/org/ministry/magic/managed/](src/main/java/org/ministry/magic/managed/), [src/main/java/org/ministry/magic/health/](src/main/java/org/ministry/magic/health/), [src/main/java/org/ministry/magic/filter/](src/main/java/org/ministry/magic/filter/), [src/main/java/org/ministry/magic/servlet/](src/main/java/org/ministry/magic/servlet/)

## Repo-Specific Conventions

- IDs are UUIDs, not database-generated integers.
- Status changes are guarded in `WizardService.updateStatus`; preserve that transition logic when adding new states or endpoints.
- Deregistration is a soft delete implemented as `SUSPENDED`, not row removal.
- Integration tests run the real app with `DropwizardAppExtension` and an isolated H2 database in PostgreSQL mode.
- Request logging for API traffic is handled by `AuditLogFilter` on `/api/*`; keep new public API paths consistent with that mapping if they should be audited.

## Common Edit Routing

- New or changed endpoint: update the resource class first, then the service, then DAO/DTOs as needed.
- New business rule: change [src/main/java/org/ministry/magic/service/WizardService.java](src/main/java/org/ministry/magic/service/WizardService.java) and add a focused unit test.
- New persisted field: update the domain model, request/response DTOs, JDBI mapper/SQL, seed/setup logic, and at least one integration test.
- New startup-managed behavior: wire it in [src/main/java/org/ministry/magic/WizardRegistryApplication.java](src/main/java/org/ministry/magic/WizardRegistryApplication.java).

## References

- API examples and local usage: [README.md](README.md)
- Test configuration: [src/test/resources/test-wizard-registry.yml](src/test/resources/test-wizard-registry.yml)
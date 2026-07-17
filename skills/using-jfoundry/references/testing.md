# Architecture And Integration Verification

Add architecture tests with the project skeleton. Copy exactly one matching template:

- Hexagonal: `assets/templates/java/HexagonalArchitectureTest.java`
- Onion Simple: `assets/templates/java/OnionSimpleArchitectureTest.java`

Replace `PACKAGE_NAME` and keep the selected annotations or rings in the analysis scope. Do not mix Hexagonal and Onion rules. Add optional CQRS or aggregate-repository rules only when the project uses those concepts; record temporary migration exceptions and remove them after cleanup.

Use `jfoundry-architecture-test` with `test` scope in the module or test aggregate that can see the packages being analyzed. Verify the narrowest relevant scope first:

```bash
mvn -pl <module> -am test
```

Use `mvn test` for a single-module project or when the change crosses modules. Add focused runtime tests for selected persistence, transaction, broker, Outbox, Inbox, or WebMVC behavior. Read `references/upstream-documentation.md` for exact architecture-rule entrypoints and runtime test requirements.

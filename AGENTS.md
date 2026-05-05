# AGENTS

## Notes
- This repo is the monitor helper library used by monitor projects such as `grayhat-monitor`.
- Assume downstream monitor code usually catches `Exception`, not `Throwable`.
- Helper methods that represent expected monitor failures must throw checked `Exception`, or catch the failure and add a readable monitor error. Do not throw unchecked `RuntimeException`, `IllegalStateException`, or `IllegalArgumentException` for expected monitor/reporting failures because those can be missed or handled inconsistently in executor and wrapper code.
- Reserve unchecked exceptions only for true programmer bugs where crashing fast is intentional and not a monitor alert path.
- When changing helper method signatures, check downstream monitor callers for compile impact.
- For delayed alerts on failures that must persist before notifying, prefer `BaseTest.checkIfFailurePersists(...)` or `BaseTest.getErrorsIfFailurePersists(...)`. The persisted state machine lives in `PersistentFailureSignal`; use the `BaseTest` wrappers from monitor tests so keys include the concrete test class. If retries are involved, use `AbstractRetryingTest.retryAndGetErrorsIfFailurePersists(...)`; it returns persistent/final errors and does not add them to `errors`, so callers decide what to report.
- Retry actions used with `AbstractRetryingTest.retry(...)` or `retryAndGetErrorsIfFailurePersists(...)` must return attempt-local `Vector<String>` errors and must not mutate the shared `errors` vector directly. Shared `errors` mutation breaks retry suppression and persistent-failure classification, especially with parallel checks.
- Never run `mvn install` manually for this repo unless the user explicitly asks for it. Compile it with `mvn test -DskipTests`; publishing/syncing/installing the dependency is a separate user-controlled step.
- Deploy with `mvn -U clean deploy -Dgpg.skip=true` (note for the user.)

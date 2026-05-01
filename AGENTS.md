# AGENTS

## Notes
- This repo is the monitor helper library used by monitor projects such as `grayhat-monitor`.
- Assume downstream monitor code usually catches `Exception`, not `Throwable`.
- Helper methods that represent expected monitor failures must throw checked `Exception`, or catch the failure and add a readable monitor error. Do not throw unchecked `RuntimeException`, `IllegalStateException`, or `IllegalArgumentException` for expected monitor/reporting failures because those can be missed or handled inconsistently in executor and wrapper code.
- Reserve unchecked exceptions only for true programmer bugs where crashing fast is intentional and not a monitor alert path.
- When changing helper method signatures, check downstream monitor callers for compile impact.
- Never run `mvn install` manually for this repo unless the user explicitly asks for it. Compile it with `mvn test -DskipTests`; publishing/syncing/installing the dependency is a separate user-controlled step.
- Deploy with `mvn -U clean deploy -Dgpg.skip=true` (note for the user.)

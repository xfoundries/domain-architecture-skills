# JFoundry Version Selection

Resolve the JFoundry version before choosing a BOM or dependencies.

## Existing Projects

Preserve the version the project already selects. Inspect the relevant `pom.xml` files first. If the jfoundry version is selected indirectly through imported dependency management, a parent, or a property whose value is not visible there, use the project's Maven Wrapper to inspect the effective POM or dependency tree. Do not upgrade jfoundry merely because a new capability is requested. Report conflicting selected or resolved versions instead of silently choosing one.

Preserve existing Java and runtime baselines unless the user asks to change them. Check that they remain compatible with the selected jfoundry release.

Resolve the selected JFoundry line's compatibility documentation to determine its Java compile and
runtime baseline. When the user requests a baseline migration, update compiler configuration, CI, and
user-facing prerequisites together, then verify the complete consumer build on the selected baseline.
Do not infer a baseline from a different JFoundry line or from the framework's development branch.

## New Projects

If the user specifies a JFoundry version, preserve it and let Maven resolve and validate it. Otherwise, query Maven Central for the current stable JFoundry BOM identified by the official release documentation. Exclude `SNAPSHOT`, alpha, beta, milestone, and release-candidate (`RC`) versions.

Show the selected version and pin that exact value in Maven configuration. Never generate `LATEST`, `RELEASE`, version ranges, or other dynamic selectors.

If Maven Central is unavailable, explicitly ask the user for a version. If no stable release exists, ask before selecting a snapshot or other prerelease. Never guess a version from memory.

Choose the new project's Java baseline from the selected jfoundry release's requirements and the chosen runtime's compatibility. Do not choose a context-free newest Java version.

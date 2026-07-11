# JFoundry Version Selection

Resolve the jfoundry version before choosing a BOM or copying dependency templates.

## Existing Projects

Preserve the version the project already selects. Inspect the relevant `pom.xml` files first. If the jfoundry version is selected indirectly through imported dependency management, a parent, or a property whose value is not visible there, use the project's Maven Wrapper to inspect the effective POM or dependency tree. Do not upgrade jfoundry merely because a new capability is requested. Report conflicting selected or resolved versions instead of silently choosing one.

Preserve existing Java and runtime baselines unless the user asks to change them. Check that they remain compatible with the selected jfoundry release.

## New Projects

If the user specifies a jfoundry version, preserve it and let Maven resolve and validate it. Otherwise, query Maven Central for the canonical `io.github.xfoundries:jfoundry-dependencies` BOM and select its latest stable release. Exclude `SNAPSHOT`, alpha, beta, milestone, and release-candidate (`RC`) versions.

Show the selected version and pin that exact value wherever `JFOUNDRY_VERSION` is replaced. Never generate `LATEST`, `RELEASE`, version ranges, or other dynamic selectors.

If Maven Central is unavailable, explicitly ask the user for a version. If no stable release exists, ask before selecting a snapshot or other prerelease. Never guess a version from memory.

Choose the new project's Java baseline from the selected jfoundry release's requirements and the chosen runtime's compatibility. Do not choose a context-free newest Java version.

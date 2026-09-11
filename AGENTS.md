# Project Agents.md Guide

This is a [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) project that embeds a [MoonBit](https://docs.moonbitlang.com) `wasm` module, interpreted via [Chasm](https://github.com/CharlieTap/chasm), to validate whether a single KMP host can run MoonBit-compiled logic on both iOS and Android.

You can browse and install extra MoonBit agent skills here:
<https://github.com/moonbitlang/skills>

## Language Convention

This project may be released publicly. All of the following must be written in **English**:

- Commit messages
- Code comments
- Documentation (including `AGENTS.md`, `README.md`, etc.)
- Test names
- Error messages

## Project Structure

- **`guest/`** — a self-contained MoonBit module (its own `moon.mod`) compiled to `wasm`. This is the "guest" side of a wasm host/guest relationship: MoonBit is always the guest here, regardless of which KMP target is acting as host. It never touches `moon.mod` at the repository root, because the repository's primary build system is Gradle, not `moon` — see `docs/todo.md` for why this project deliberately reverses the module layout used by its sibling project `exp-mbt-wasmkit-ios`.
- **`shared/`** — the KMP module that consumes `guest.wasm` via Chasm's build-time Kotlin binding generator, and exposes the call surface to each platform target (`jvm`, `iosSimulatorArm64`, `android`).
- **`iosApp/`** — a Tuist-managed Xcode project embedding `shared` as a Kotlin/Native framework (`Shared.framework`, via the `embedAndSignAppleFrameworkForXcode` direct-integration task — see [Kotlin's docs](https://kotlinlang.org/docs/multiplatform-direct-integration.html)), targeting `iosSimulatorArm64`.
- **`androidApp/`** — a plain `com.android.application` module depending on `shared` directly (a same-Gradle-build project dependency, unlike `iosApp/`'s cross-toolchain framework embedding), targeting `android`.
- **`docs/todo.md`** — the decision log: what's been verified, what broke, and why. Read it before changing the `guest`/`shared` boundary or the milestone scope.

This project exists specifically because `exp-mbt-wasmkit-ios/docs/todo.md` surveyed Chasm and declined to pursue it *within that project's scope* (a Swift/WasmKit iOS host), while flagging it as a real alternative worth evaluating on its own. This repository is that evaluation.

## Coding convention

### MoonBit (`guest/`)

- MoonBit code is organized in block style, each block is separated by `///|`, the order of each block is irrelevant. In some refactorings, you can process block by block independently.
- Try to keep deprecated blocks in a file called `deprecated.mbt`.
- Keep the exported surface minimal and purpose-built for whatever call shape is currently being verified — this is not a general-purpose FFI library, mirroring `exp-mbt-wasmkit-ios/guest`'s convention.

### Kotlin (`shared/`, `iosApp/`, `androidApp/`)

- Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- A formatter/linter (e.g. ktlint) will be added once the Gradle project is scaffolded; not yet decided.

## Tooling

MoonBit tooling below applies only within `guest/` (run `cd guest && moon ...`, since `moon.mod` lives there, not at the repository root):

- `moon fmt` formats MoonBit source.
- `moon ide` provides project navigation helpers like `peek-def`, `outline`, and `find-references`. See $moonbit-agent-guide for details.
- `moon info` updates the generated interface (`.mbti`) of the package. If nothing in `.mbti` changes, the change has no visible effect on external consumers (here, Chasm's binding generator) — typically a safe refactoring.
- `moon test` runs tests. MoonBit supports snapshot testing; when changes affect outputs, run `moon test --update` to refresh snapshots.
- In the last step of a `guest/` change, run `moon info && moon fmt` inside `guest/` and check the `.mbti` diff is expected.

For local quality verification:

- **pre-commit hooks** — catch formatting and lint issues on every commit (`pre-commit run --all-files` to run manually).
- **`just verify`** — MoonBit check across backends inside `guest/`; will grow a Kotlin/Gradle equivalent once `shared/` exists.

## Development Philosophy

### Red/Green TDD (Detroit school)

- Red → Green → Refactor cycle strictly followed
- Use real objects; mocks are only permitted at external boundaries (file system, external API, network)
- Write tests BEFORE implementation; run tests AFTER implementation
- Discuss coverage targets with the user before starting implementation
- Exception: exploratory spikes (e.g. verifying whether Chasm's generated bindings can call a given MoonBit export shape) may skip test-first with explicit user agreement; discard or rewrite the spike as a proper implementation afterward

### Domain Object Design

- Rich domain objects: pair data and logic in the same type
- Prefer immutability; avoid mutable state unless necessary
- Distinguish entities (identity-based) from value objects (value-based)
- Enforce layer boundaries through abstract types; no direct dependency on concrete implementations

### Evergreen Tests

- Test names describe WHAT business rule is being verified, not HOW
- Test names must not reference implementation details
- Test code serves as living documentation of the system's behavior

### Code Comments

- Do NOT write code comments unless explicitly permitted by the user
- Let the code speak for itself; let tests document the behavior
- Code = How, Tests = What, Commit messages = Why

## Git Conventions

### Format

```text
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

| Type | Description |
| :--- | :--- |
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Code style (formatting, whitespace) |
| `refactor` | Code change that is neither a fix nor a feature |
| `tidy` | Small, safe cleanup (< 2 min; no behavior change) |
| `test` | Adding or updating tests |
| `chore` | Build process, tooling, or config changes |
| `ci` | CI/CD pipeline changes (GitHub Actions, workflows) |
| `perf` | Performance improvement |

### Scopes

Scope is optional; use the area name when the change targets a specific part of the tree (e.g., `guest`, `shared`, `iosApp`, `androidApp`). Omit for project-wide changes.

### Type vs. Scope Precedence

The type vocabulary above mixes two axes: an **impact axis** (`feat`, `fix`, `perf`, `refactor` — the SemVer-relevant effect of a change) and a **domain axis** (`docs`, `style`, `test`, `chore`, `ci`, `tidy` — a layer with no runtime/SemVer effect). When a change is fully contained within a domain, use that domain as `type` (e.g. `docs: fix typo`); do not use it as `scope` on an impact-axis type (avoid `fix(docs): ...`). `scope` sub-divides whatever `type` already established (e.g. `feat(shared)`); it is not a substitute classification axis.

### Subject Line

- Use the imperative mood: "add", "fix", "remove" — not "added" or "adds"
- 72 characters max
- No trailing period

### Body (optional)

- Explain **why**, not what — the diff already shows what changed
- Leave one blank line between subject and body

### Footer (optional)

- `BREAKING CHANGE: <description>` for breaking changes
- `Closes #123` or `Fixes #456` to link issues

### Branch naming

`feat/xxx`, `fix/xxx`, `docs/xxx`

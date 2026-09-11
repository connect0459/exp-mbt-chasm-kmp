# Contributing

## Prerequisites

- [MoonBit toolchain](https://www.moonbitlang.com/download/) — `moon` CLI, for `guest/`
- [just](https://just.systems/) — task runner
- [pre-commit](https://pre-commit.com/) — hook runner
- JDK + Gradle — for `shared/`
- [Xcode](https://developer.apple.com/xcode/) and [Tuist](https://tuist.dev/) — for the `iosApp/` host
- [Android SDK](https://developer.android.com/studio) (platform 36, an `arm64-v8a` emulator image) — for the `androidApp/` host

## Setup

```sh
git clone https://github.com/connect0459/exp-mbt-chasm-kmp
cd exp-mbt-chasm-kmp
just setup
```

`just setup` runs `moon update` inside `guest/` to fetch package dependencies, and installs the pre-commit hooks (`pre-commit install`).

### pre-commit hooks

To run all hooks manually:

```sh
pre-commit run --all-files
```

## Project structure

- `guest/` — a self-contained MoonBit module (its own `moon.mod`), compiled to `wasm`. This is the "guest" side of a wasm host/guest relationship, the same shape used by the sibling project `exp-mbt-wasmkit-ios` (there, the host is Swift/WasmKit; here, it is Kotlin/Chasm).
- `shared/` — the KMP module consuming `guest.wasm` via Chasm's build-time Kotlin binding generator, targeting `jvm`, `iosSimulatorArm64`, and `android`.
- `iosApp/` — the Tuist-managed Xcode project embedding `shared` as a Kotlin/Native framework, via the `embedAndSignAppleFrameworkForXcode` direct-integration task.
- `androidApp/` — a plain `com.android.application` module depending on `shared` as a regular Gradle project dependency.
- `docs/todo.md` — the log of what's been verified, what broke, and why. Read it before changing the `guest`/`shared` boundary.

## Development workflow

| Command | Purpose |
| :--- | :--- |
| `cd guest && moon test` | Run all `guest/` tests |
| `cd guest && moon test --target wasm` | Run `guest/` tests on a specific backend |
| `cd guest && moon fmt` | Format `guest/` source files |
| `cd guest && moon check` | Type-check `guest/` without building |
| `cd guest && moon info` | Regenerate `guest/`'s `.mbti` interface file |
| `just verify` | Run the full `guest/` CI-equivalent check locally |
| `./gradlew lintKotlin` / `formatKotlin` | Lint / auto-format Kotlin sources across all modules |
| `./gradlew :shared:jvmTest :shared:iosSimulatorArm64Test :shared:testAndroidHostTest` | Run `shared/` tests on the fast (non-device) targets |
| `./gradlew :shared:connectedAndroidDeviceTest` | Run `shared/` tests on a connected Android emulator/device |
| `just ios-generate` | `build-guest-wasm`, then generate the Tuist-managed `iosApp/` Xcode project |
| `./gradlew :androidApp:installDebug` | Build and install `androidApp/` on a connected Android emulator/device |

Before opening a pull request touching `guest/`, run:

```sh
just verify
```

This mirrors the CI matrix: it checks all four backends (`js`, `wasm`, `wasm-gc`, `native`) for `guest/`.

## Testing guidelines

This project follows **Red → Green → Refactor** (Detroit-school TDD):

- Write a failing test first, then implement.
- Use real objects; mocks are only permitted at external boundaries.
- Test names describe **what business rule** is verified, not how.
- Exception: exploratory spikes (verifying whether Chasm's generated bindings can call a given MoonBit export shape, or whether a KMP target builds at all) may skip test-first with explicit agreement — discard or rewrite as a proper implementation afterward. This is expected to be the norm early on, as with `exp-mbt-wasmkit-ios`.

## Commit format

```text
<type>(<scope>): <subject>
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `tidy`, `test`, `chore`, `ci`, `perf`

**Scope**: area name when the change targets one specific part of the tree (`guest`, `shared`, `iosApp`, `androidApp`); omit for project-wide changes.

**Subject**: imperative mood, 72 characters max, no trailing period.

Examples:

```text
feat(guest): add increment export for the Chasm binding spike
fix(shared): guard against a missing Chasm-generated binding at call time
docs: record Milestone 1 binding-generation results
```

## Pull request process

1. Fork the repository and create a branch: `feat/xxx`, `fix/xxx`, `docs/xxx`.
2. Follow the Red → Green → Refactor cycle for `guest/` changes.
3. Run `just verify` and commit any resulting diffs.
4. If the change touches `guest/`'s exported API, run `moon info` (inside `guest/`) and verify the `.mbti` diff is expected.
5. If the change affects `shared/`/`iosApp/`/`androidApp/`, confirm the relevant platform target still builds and runs — `./gradlew :shared:jvmTest :shared:iosSimulatorArm64Test :shared:testAndroidHostTest`, and/or a simulator run via `just ios-generate` or an emulator run via `./gradlew :shared:connectedAndroidDeviceTest`.
6. Update `docs/todo.md` if the change resolves an open question or surfaces a new one — this file is the project's primary record, more so than commit messages alone.
7. Open a pull request.

## Code style

- No code comments unless the **why** is genuinely non-obvious.
- Prefer immutability; avoid mutable state unless necessary.
- Keep `guest/`'s exported surface minimal and purpose-built for whatever is currently being verified — this is not a general-purpose FFI library.
- All user-facing strings (test names, error messages, doc comments) must be in **English**.

# todo - mbt-chasm-kmp

Current state: **Milestone 0 (governance/tooling scaffold) is in progress.** No MoonBit or Kotlin code exists yet.

---

## Background

`mbt-wasmkit-ios/docs/todo.md` surveyed [`CharlieTap/chasm`](https://github.com/CharlieTap/chasm) — a Wasm interpreter written in Kotlin Multiplatform, documented as legal on both iOS and Android — as an alternative to the "Swift+WasmKit on iOS, Java/Kotlin+Chicory on Android" per-platform split that project and [`Nanaloveyuki/wasee-moon`](https://github.com/Nanaloveyuki/wasee-moon) each independently reached. It declined to pursue Chasm there, since doing so would mean the iOS host app itself is written against Kotlin Multiplatform instead of native Swift — a materially different application architecture than that project's Swift/WasmKit scope.

This project is that evaluation, carved out separately so the decision doesn't have to be made inside `mbt-wasmkit-ios`.

## Decisions made before any code existed

- **Name**: `mbt-chasm-kmp`, not `mbt-chasm-cmp`. The goal is validating Chasm's Kotlin/Native and Kotlin/JVM call behavior, not Compose Multiplatform UI sharing; naming it `-cmp` would have silently expanded scope to include UI-layer feasibility, which is not the question being asked.
- **`guest/` is a self-contained MoonBit module** (its own `moon.mod`), not part of a repository-root `moon.mod` the way `mbt-wasmkit-ios` is structured. That project's whole repository *is* the MoonBit module, with `ios/` as the secondary consumer; this repository's primary build system is Gradle/KMP, so the root should not also claim to be a MoonBit module root. `guest/` plays the same wasm-guest role either way — only the location of `moon.mod` differs, reflecting which side is primary in each project.
- **Milestone 1 scope is (B): actual iOS integration, not just a Kotlin/Native binary.** Considered scoping Milestone 1 to (A) — confirm Chasm's generated bindings work on a Kotlin/Native target at all, with no Xcode packaging — before touching iOS app integration, mirroring how `mbt-wasmkit-ios` separated "is this technically feasible" (Milestone 1) from "does it integrate into an actual iOS app" (Milestone 2). That separation was warranted there because iOS-target code generation itself was in doubt (native/llvm backend's triple support, `libmoonbitrun.o` being macOS-only). Kotlin/Native → iOS has no equivalent doubt; it's an established, mainstream KMP use case. The only open question is Chasm-specific (does its interpreter/binding generation work correctly once embedded in an iOS app bundle), so testing it only via a bare Kotlin/Native binary wouldn't actually de-risk what's in doubt. Decided to keep (B) as the milestone goal, but preserve the failure-isolation benefit of (A) by ordering the checklist within Milestone 1:
  1. Generate Chasm bindings for a MoonBit `wasm` export and call them from an `iosSimulatorArm64` Kotlin/Native test, no Xcode packaging.
  2. Only after that passes, package `shared` into an `iosApp/` Xcode project and confirm the same call on a simulator.
- **No `publish.yml` (mooncakes) for now.** `mbt-wasmkit-ios` carries one despite its own `SECURITY.md` describing itself as "not a published library" — a leftover from porting `starlark-mbt`/`urllib-mbt` boilerplate without re-evaluating fit for an experimental repo. This project is even more clearly a validation spike, so the inconsistency wasn't carried forward. Can be added later if `guest/` ever becomes a reusable package worth publishing.

## Milestone 0: Project scaffold

- [x] Ported `AGENTS.md`/`CLAUDE.md`, `.markdownlint.json`, `.pre-commit-config.yaml`, `apm.yml`, `.github/` (CI, Copilot setup, issue/PR templates), `justfile`, `LICENSE`, `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, `SECURITY.md`, `README.md` from `mbt-wasmkit-ios`, adapting each for this project's inverted structure (Gradle/KMP-primary, `moon.mod` inside `guest/` rather than at the repository root) and narrower scope (no `publish.yml`; see decisions above)
- [ ] `git init` and initial commit (not done yet — awaiting user go-ahead, since creating the repository's history is easy to get wrong once and awkward to undo)
- [ ] Create the GitHub remote (`connect0459/mbt-chasm-kmp`) and push
- [ ] Run `apm install` (or equivalent) to populate `apm_modules/`/`.claude/skills` from `apm.yml`

## Milestone 1: Minimal Chasm round trip (MoonBit `wasm` → Chasm → Kotlin/Native call, then iOS simulator)

Goal: verify whether Chasm can load a MoonBit-compiled `wasm` module, generate Kotlin bindings for it at build time, and call it correctly — first from a bare Kotlin/Native test, then packaged into an iOS simulator app — before deciding whether this route is viable as `mbt-wasmkit-ios`'s Milestone 2 replacement for Android support.

- [ ] Scaffold `guest/` as its own MoonBit module (`moon new`-equivalent, `moon.mod` at `guest/`, `preferred_target = "wasm"`)
- [ ] TDD: write a test for `increment(value : Int) -> Int` first (Red), then implement with `#export_name("increment")` (Green) — mirrors `mbt-wasmkit-ios/guest`'s first export, so any difference in behavior is attributable to the host (Chasm) rather than the guest function
- [ ] Scaffold the KMP Gradle project (`settings.gradle.kts`, `shared/` module, `iosSimulatorArm64` + a JVM target)
- [ ] Add Chasm's Gradle plugin to `shared/`, point it at `guest.wasm`, confirm it generates a Kotlin class/interface pair for `increment`
- [ ] Call `increment` from a `shared` test running on the `iosSimulatorArm64` Kotlin/Native target — no Xcode packaging yet
- [ ] Call `increment` from a `shared` test running on the JVM target, for comparison
- [ ] Scaffold `iosApp/` (Xcode project embedding `shared`'s Kotlin/Native framework), confirm the same call succeeds on an iOS simulator
- [ ] Record any call-overhead measurement using the same methodology as `mbt-wasmkit-ios` (separate one-time setup cost from per-call cost, loop over many calls), so the two projects' numbers are comparable

## Open questions

- [ ] Where does Chasm's Gradle plugin expect to read the `.wasm` input from — a resource path convention, a build-time input file path, something else? Unknown until actually wired up; `mbt-wasmkit-ios` hit a non-obvious equivalent (Tuist's `resources:` glob timing) here, so budget time for a similar surprise
- [ ] Does Chasm support only core `wasm`, or also `wasm-gc`? `mbt-wasmkit-ios`/`wasee-moon` both needed to target plain `wasm` because their respective hosts (WasmKit, Chicory) don't implement the Wasm GC proposal. Unconfirmed for Chasm — check before assuming the same constraint applies
- [ ] How does Chasm's generated binding surface handle a MoonBit function returning a heap-boxed value (e.g. a tuple, as in `mbt-wasmkit-ios`'s `move_point` spike) — does it expose linear-memory reads similarly, or does its Kotlin binding generator abstract that away?

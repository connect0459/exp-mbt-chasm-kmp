# mbt-chasm-kmp

An experimental spike evaluating whether [Chasm](https://github.com/CharlieTap/chasm) (a Wasm interpreter written in Kotlin Multiplatform) can serve as a single host runtime for MoonBit-compiled `wasm` logic on both iOS and Android.

## Background

The sibling project [`mbt-wasmkit-ios`](https://github.com/connect0459/mbt-wasmkit-ios) verified a MoonBit `wasm` guest → Swift/[WasmKit](https://github.com/swiftwasm/WasmKit) host round trip on iOS, but is iOS-only by construction (WasmKit is a Swift package). Its `docs/todo.md` surveyed Chasm as a KMP-based alternative that could in principle unify the iOS and Android hosts into one implementation, but declined to pursue it within that project's scope — adopting Chasm there would mean rewriting the host app itself in Kotlin Multiplatform instead of Swift, a materially different architecture.

This project is that evaluation, carved out on its own so the decision doesn't have to be made inside `mbt-wasmkit-ios`.

## Status

`increment(41) = 42` runs end-to-end: MoonBit `wasm` guest → Chasm interpreter → native UI, on both an iOS simulator (Kotlin/Native → SwiftUI) and an Android emulator (ART → a plain Android `View`).

## Project structure

- `guest/` — MoonBit module compiled to `wasm`
- `shared/` — KMP module consuming `guest.wasm` via Chasm's build-time binding generator, targeting `jvm`, `iosSimulatorArm64`, and `android`
- `iosApp/` — Tuist-managed Xcode project embedding `shared` as a Kotlin/Native framework
- `androidApp/` — Android application module depending on `shared` directly

## Documentation

See [docs/todo.md](https://github.com/connect0459/mbt-chasm-kmp/blob/main/docs/todo.md) for the milestone plan and decision log — the project's primary record of what's been verified, what broke, and why.

## Contributing

See [CONTRIBUTING.md](https://github.com/connect0459/mbt-chasm-kmp/blob/main/CONTRIBUTING.md).

## License

[Apache-2.0](https://github.com/connect0459/mbt-chasm-kmp/blob/main/LICENSE)

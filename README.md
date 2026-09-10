# mbt-chasm-kmp

An experimental spike evaluating whether [Chasm](https://github.com/CharlieTap/chasm) (a Wasm interpreter written in Kotlin Multiplatform) can serve as a single host runtime for MoonBit-compiled `wasm` logic on both iOS and Android.

## Background

The sibling project [`mbt-wasmkit-ios`](https://github.com/connect0459/mbt-wasmkit-ios) verified a MoonBit `wasm` guest → Swift/[WasmKit](https://github.com/swiftwasm/WasmKit) host round trip on iOS, but is iOS-only by construction (WasmKit is a Swift package). Its `docs/todo.md` surveyed Chasm as a KMP-based alternative that could in principle unify the iOS and Android hosts into one implementation, but declined to pursue it within that project's scope — adopting Chasm there would mean rewriting the host app itself in Kotlin Multiplatform instead of Swift, a materially different architecture.

This project is that evaluation, carved out on its own so the decision doesn't have to be made inside `mbt-wasmkit-ios`.

## Status

`increment(41) = 42` runs end-to-end: MoonBit `wasm` guest → Chasm interpreter (Kotlin/Native) → SwiftUI, on an iOS simulator. See [`docs/todo.md`](docs/todo.md) for the milestone plan and decision log.

## Project structure

- `guest/` — MoonBit module compiled to `wasm`
- `shared/` — KMP module consuming `guest.wasm` via Chasm's build-time binding generator, targeting `jvm` and `iosSimulatorArm64`
- `iosApp/` — Tuist-managed Xcode project embedding `shared` as a Kotlin/Native framework

See [`CONTRIBUTING.md`](CONTRIBUTING.md) for setup and workflow details.

## License

[Apache-2.0](LICENSE)

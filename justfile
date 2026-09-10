# Setup after clone
setup:
    cd guest && moon update
    just build-guest-wasm
    pre-commit install

# Run tests for a single target (e.g. `just test-target wasm-gc`)
test-target target:
    cd guest && moon check --deny-warn --target {{target}}
    cd guest && moon test --target {{target}}

# Verify code quality and all targets (matches CI)
verify:
    cd guest && moon fmt --check
    for t in js wasm wasm-gc native; do \
        just test-target $t; \
    done

# Build guest.wasm and copy it into shared/'s commonMain resources, where
# Chasm's Gradle plugin (configured in shared/build.gradle.kts) reads it
# from to generate Kotlin bindings. Required before any Gradle task that
# touches shared/ — Chasm's codegen fails immediately without this file.
build-guest-wasm:
    cd guest && moon build --target wasm --release
    cp guest/_build/wasm/release/build/mbt_chasm_kmp_guest.wasm shared/src/commonMain/resources/guest.wasm

# iosApp/ recipes will be added once that Xcode project is scaffolded.

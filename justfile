# Setup after clone
setup:
    cd guest && moon update
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

# Gradle/Kotlin recipes (build-shared, ios-generate, etc.) will be added
# once `shared/` and `iosApp/` are scaffolded.

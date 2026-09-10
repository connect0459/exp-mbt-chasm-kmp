<!-- # PULL_REQUEST_TEMPLATE -->

<!-- Remove unnecessary sections to keep the review focused -->

## Related Links

- Issues
  - <!-- <https://github.com/connect0459/mbt-chasm-kmp/issues/xxx> -->
- PRs
  - <!-- <https://github.com/connect0459/mbt-chasm-kmp/pull/xxx> -->

## [Required] Overview

- Describe the problem being solved, its background, and what changes when this PR is merged.
- Links to specs, design documents, or other references are welcome.

```txt
It is difficult to review without knowing the specifications and background.
```

## Scope of Change

- [ ] `guest/` (MoonBit)
- [ ] `shared/` (Kotlin Multiplatform)
- [ ] `iosApp/` (Xcode/iOS host)
- [ ] Tooling / CI
- [ ] Documentation

## Breaking Changes

- [ ] No breaking changes
- [ ] Breaking changes (describe below)

<!--
If this changes `guest/`'s public API (.mbti diff) or `shared/`'s generated Chasm
bindings, describe what breaks and why the breakage is justified.
-->

## Deferred Items and TODOs

- Items intentionally deferred and the reasons why.

```txt
If you deferred something due to time constraints, document it here.
Reviewers cannot tell whether something was intentionally skipped or overlooked
without this information.
```

## Test Items

- Describe any test considerations beyond unit tests.
- If `guest/` changed, note which backends were validated (js / wasm / wasm-gc / native).
- If `shared/`/`iosApp/` changed, note which platform targets were validated.

## [Required] Quality Checklist

**Please check all items before merging.**

- [ ] **CI Workflow Execution**: Full quality check completed by manually running `Run workflow` in [Actions](../actions/workflows/ci.yml)
- [ ] **Code Comments**: Code comments and function/method-level doc-comments are in sync with the changes
- [ ] **Reference Docs**: `docs/todo.md` is updated if this change resolves an open question or surfaces a new one; `guest/README.mbt.md` and `.mbti` are updated for any `guest/` public API change

> **Important**: This checklist ensures quality. Please verify all items before requesting review.

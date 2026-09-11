plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.chasm)
}

// Chasm's codegen output and our own generated GuestWasmBytes.kt aren't
// ours to style. The original choice here, JLLeitschuh/ktlint-gradle, has a
// still-open upstream bug where excluding generated Kotlin Multiplatform
// sources doesn't work (#746, #751) — tried both its extension-level
// `filter {}` DSL and a task-level PatternFilterable.exclude, neither took
// effect. Switched to kotlinter instead, matching how Chasm's own build
// excludes its own generated sources for the exact same reason (see
// linting-conventions.gradle.kts in the chasm repo) — same technique,
// applied on kotlinter's task type instead.
tasks.withType<org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask>().configureEach {
    exclude { element -> element.file.path.contains("/generated/") }
}

chasm {
    modules {
        create("GuestService") {
            binary = layout.projectDirectory.file("src/commonMain/resources/guest.wasm")
            packageName = "dev.connect0459.mbtchasmkmp.shared.guest"
        }
    }
}

// Chasm's codegen only generates the interface/impl class shape from
// guest.wasm at build time; the raw bytes still have to be supplied by us
// at runtime (GuestServiceImpl's `binary` constructor parameter). Embedding
// them as a compiled-in constant works identically on every KMP target
// (jvm, iosSimulatorArm64, and later Android) with no platform-specific
// resource/bundle lookup, and keeps working once guest.wasm is bundled into
// a real iOS app later.
val guestWasmBytesDir = layout.buildDirectory.dir("generated/guestWasmBytes")

val generateGuestWasmBytes =
    tasks.register("generateGuestWasmBytes") {
        val wasmFile = layout.projectDirectory.file("src/commonMain/resources/guest.wasm")
        val outputDir = guestWasmBytesDir

        inputs.file(wasmFile)
        outputs.dir(outputDir)

        doLast {
            val packageDir = outputDir.get().dir("dev/connect0459/mbtchasmkmp/shared/guest").asFile
            packageDir.mkdirs()
            val bytes = wasmFile.asFile.readBytes()
            val literal = bytes.joinToString(", ") { it.toString() }
            packageDir.resolve("GuestWasmBytes.kt").writeText(
                """
                package dev.connect0459.mbtchasmkmp.shared.guest

                internal val GUEST_WASM_BYTES: ByteArray = byteArrayOf($literal)

                """.trimIndent(),
            )
        }
    }

kotlin {
    // Pins the JDK Gradle uses for jvm/Android compilation regardless of
    // the invoking shell's JAVA_HOME (matches CI's actions/setup-java
    // version) — no env var to set or forget.
    jvmToolchain(21)

    jvm()
    android {
        namespace = "dev.connect0459.mbtchasmkmp.shared"
        compileSdk = 36
        minSdk = 24

        // androidHostTest: fast local-JVM sanity check (like commonTest,
        // but against Android SDK stubs). androidDeviceTest: the real
        // deliverable for this milestone — runs on an actual
        // emulator/device (ART), same reasoning as testing on
        // iosSimulatorArm64 instead of only jvm.
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }
    iosSimulatorArm64 {
        // Required for the `embedAndSignAppleFrameworkForXcode` Gradle task
        // (used by iosApp/'s Run Script build phase) to register at all.
        binaries.framework {
            baseName = "Shared"
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateGuestWasmBytes.map { guestWasmBytesDir })
            dependencies {
                // Chasm's Gradle plugin only adds what its own generated
                // code needs to compile; using its lower-level embedding
                // API directly (module/store/instance/invoke/readInt, used
                // by GuestBenchmark's move_point measurement) requires this
                // runtime artifact as an explicit dependency.
                implementation(libs.chasm.runtime)
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        // The Android KMP plugin declares an androidx.test.runner.AndroidJUnitRunner
        // instrumentation in the test manifest automatically, but doesn't add the
        // artifact that class lives in — without it, the on-device test APK crashes
        // with ClassNotFoundException before running anything.
        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.test.ext.junit)
            }
        }
    }
}

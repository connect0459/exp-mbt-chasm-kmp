plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinter)
}

kotlin {
    jvm()
    iosSimulatorArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

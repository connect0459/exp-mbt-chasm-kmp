plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
}

allprojects {
    group = "dev.connect0459.mbtchasmkmp"
    version = "0.1.0"
}

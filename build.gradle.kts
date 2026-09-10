plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    group = "dev.connect0459.mbtchasmkmp"
    version = "0.1.0"
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.connect0459.mbtchasmkmp.androidapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.connect0459.mbtchasmkmp.androidapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared"))
}

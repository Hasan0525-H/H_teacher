plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val ciRunNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0
val developmentVersionCode = 1000 + ciRunNumber
val developmentVersionName = if (ciRunNumber > 0) "0.1.0-dev.$ciRunNumber" else "0.1.0-dev"

android {
    namespace = "com.hasan0525.hteacher"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hasan0525.hteacher"
        minSdk = 26
        targetSdk = 36
        versionCode = developmentVersionCode
        versionName = developmentVersionName
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":shared"))
}

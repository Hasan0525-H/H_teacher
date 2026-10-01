plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val ciRunNumber = System.getenv("GITHUB_RUN_NUMBER")
    ?.toIntOrNull()
    ?: 0

val developmentVersionCode = 1000 + ciRunNumber
val developmentVersionName = if (ciRunNumber > 0) {
    "0.1.0-dev." + ciRunNumber
} else {
    "0.1.0-dev"
}

val aiGatewayUrl = (System.getenv("AI_GATEWAY_URL")
    ?.trim()
    ?.takeIf { it.isNotBlank() }
    ?: "https://hteacher-ai-gateway.kd-alsalhi.workers.dev")
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

android {
    namespace = "com.hasan0525.hteacher"
    compileSdk = 36

    signingConfigs {
        create("development") {
            storeFile = rootProject.file("keystore/hteacher-debug.jks")
            storePassword = "android"
            keyAlias = "hteacherdebug"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "com.hasan0525.hteacher"
        minSdk = 26
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = developmentVersionCode
        versionName = developmentVersionName
        buildConfigField(
            "String",
            "AI_GATEWAY_URL",
            "\"" + aiGatewayUrl + "\""
        )
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("development")
        }

        release {
            signingConfig = signingConfigs.getByName("development")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions.managedDevices.localDevices {
        create("pixel2Api30") {
            device = "Pixel 2"
            apiLevel = 30
            systemImageSource = "aosp-atd"
        }
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    val roomVersion = "2.8.5"

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.datastore:datastore-preferences:1.2.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.7.0")
}

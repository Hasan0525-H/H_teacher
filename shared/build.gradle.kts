plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm()
    androidTarget()

    sourceSets {
        val commonMain by getting
        val commonTest by getting
    }
}

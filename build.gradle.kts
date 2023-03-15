plugins {
    kotlin("multiplatform") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
}

group = "site.j2k"
version = "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")

    if (!hostOs.startsWith("Windows")) {
        throw GradleException("Host OS is not supported")
    }

    mingwX64("native").apply {
        binaries {
            executable("input-post-processor", listOf(RELEASE)) {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        val nativeTest by getting
    }
}

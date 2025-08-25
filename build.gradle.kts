import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "${KOTLIN_VERSION}"
    // Apply the Java plugin to get the 'jar' task and standard configurations.
    id("java")
}

group = "${PACKAGE_NAME}"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

val androidHome = System.getenv("ANDROID_HOME")
if (androidHome == null || androidHome.isBlank()) {
    throw GradleException("Error: ANDROID_HOME environment variable is not set. Please point it to your Android SDK location.")
}

dependencies {
    // Compile-only dependency on Android framework.
    compileOnly(files("\$androidHome/platforms/android-${ANDROID_API_LEVEL}/android.jar"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    // Set the output JAR file name for the built artifact.
    archiveBaseName.set("${PROJECT_NAME.toLowerCase()}")
    
    manifest {
        attributes["Main-Class"] = "${PACKAGE_NAME}.AppNameFetcher"
    }
}
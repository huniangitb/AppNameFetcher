import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // 硬编码 Kotlin 版本
    kotlin("jvm") version "1.9.22" 
    // 应用 Java 插件以获取 'jar' 任务
    id("java")
}

// 修改: 将 ${PACKAGE_NAME} 替换为实际的包名
group = "com.example.fetcher"
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
    // 修改: 将 ${ANDROID_API_LEVEL} 替换为实际的 API Level
    compileOnly(files("$androidHome/platforms/android-34/android.jar"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.jar {
    // 修改: 将 ${PROJECT_NAME} 替换为实际的项目名（小写）
    archiveBaseName.set("appnamefetcher")
    
    manifest {
        // 修改: 将 ${PACKAGE_NAME} 替换为实际的包名
        attributes["Main-Class"] = "com.example.fetcher.AppNameFetcher"
    }
}
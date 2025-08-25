import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion // 导入 JavaLanguageVersion

plugins {
    kotlin("jvm") version "1.9.22"
    id("java") // 应用 Java 插件以获取 'jar' 任务
}

group = "com.example.fetcher"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

// 检查 ANDROID_HOME 环境变量，以便找到 android.jar
val androidHome = System.getenv("ANDROID_HOME")
if (androidHome == null || androidHome.isBlank()) {
    throw GradleException("Error: ANDROID_HOME environment variable is not set. Please point it to your Android SDK location.")
}

// ===================================================================
// 核心修复：使用 JVM Toolchain 确保 Java 和 Kotlin 的 JVM 目标版本一致
// ===================================================================
java {
    toolchain {
        // 设置所有 Java 和 Kotlin 编译任务的目标 JVM 语言版本
        // 推荐 11 (Android 11+兼容性好) 或 8 (最广泛兼容)。
        // 这会覆盖 kotlinOptions.jvmTarget 的设置，并自动协调 compileJava。
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
// ===================================================================

dependencies {
    // Compile-only 依赖 Android 框架，用于编译时引用 Android API
    // 运行时由设备提供，不会打包进 JAR
    compileOnly(files("$androidHome/platforms/android-34/android.jar"))
}

// 之前这里的 tasks.withType<KotlinCompile> 块可以删除了，
// 因为 toolchain 已经统一处理了 JVM 目标版本。
// 如果你想手动设置 JVM 目标，就不能用 toolchain，并且需要确保与 java.sourceCompatibility/targetCompatibility 一致。

tasks.jar {
    // 设置输出 JAR 文件的名称
    archiveBaseName.set("appnamefetcher")
    
    // 配置 JAR 的 MANIFEST.MF 文件，指定主类
    manifest {
        attributes["Main-Class"] = "com.example.fetcher.AppNameFetcher"
    }
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    kotlin("jvm") version "1.9.22"
    id("java")
}

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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11)) // 保持 11，或根据需要改为 8
    }
}

dependencies {
    // Compile-only 依赖 Android 框架
    compileOnly(files("$androidHome/platforms/android-34/android.jar"))

    // ===================================================================
    // 重要：将 Kotlin 标准库声明为运行时依赖，以便打包进 Fat JAR
    // ===================================================================
    // 使用 implementation 确保 Kotlin 标准库会被打包进 JAR
    // 或者如果你想更明确控制，可以定义一个 customRuntimeClasspath
    implementation(kotlin("stdlib"))
    // 确保你的所有其他 Kotlin 模块如果依赖了 Kotlin stdlib
    // 也会被打包进来
    // implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlin_version}") // 如果你明确使用 jdk8 版本
}

tasks.jar {
    archiveBaseName.set("appnamefetcher")
    
    manifest {
        attributes["Main-Class"] = "com.example.fetcher.AppNameFetcher"
    }

    // ===================================================================
    // 核心修改：将所有运行时依赖打包进 JAR 文件
    // ===================================================================
    // 从所有的运行时 classpath 中获取文件或 zip 文件并添加到当前 JAR
    // 这会将 kotlin-stdlib 和其他任何 implementation 依赖打包进来
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    // 排除 JAR 文件中可能重复的或不需要的 META-INF 文件
    // (可选，但推荐，以避免JAR冲突)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/LICENSE*", "META-INF/NOTICE*")
}
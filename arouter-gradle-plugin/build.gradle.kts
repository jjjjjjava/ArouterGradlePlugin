@file:Suppress("SpellCheckingInspection")

plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("maven-publish")  // 添加 maven-publish 插件
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

// The project version will be used as your plugin version when publishing.
group = "io.github.jjjjjjava"
version = "1.0.3-java21"

gradlePlugin {
    plugins {
        register("ARouterPlugin") {
            id = "io.github.jjjjjjava.ARouterPlugin"
            implementationClass = "cn.jjjjjjava.arouter_gradle_plugin.ARouterPlugin"
            displayName = "Arouter AGP7.4+ plugin with Java 21 support"
            description = "Arouter AGP7.4+ plugin with Java 21 support (ASM 9.7)"
        }
    }
}

// 配置 maven-publish 发布
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "io.github.jjjjjjava"
            artifactId = "arouter-gradle-plugin"
            version = "1.0.3-java21"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    gradleApi()
    compileOnly("com.android.tools.build:gradle:8.2.2")
    compileOnly("commons-io:commons-io:2.8.0")
    compileOnly("commons-codec:commons-codec:1.15")
    // 升级 ASM 到 9.7 支持 Java 21 (class file version 65)
    compileOnly("org.ow2.asm:asm-commons:9.7")
    compileOnly("org.ow2.asm:asm-tree:9.7")
}

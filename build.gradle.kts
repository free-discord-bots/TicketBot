import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jmailen.kotlinter") version "3.10.0"
    kotlin("kapt") version "1.7.0"
}

group = "dev.minjae.ticketbot"
version = "1.0-SNAPSHOT"

val jdaKtxVersion = "bf7cd9645ca6be094cc6dea5858579e6ede5ae1e"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.minndevelopment:jda-ktx:$jdaKtxVersion")
    implementation("net.dv8tion:JDA:5.0.0-alpha.12")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.2")
    implementation("io.sentry:sentry-logback:6.0.0")
    implementation("io.requery:requery:1.6.0")
    kapt("io.requery:requery-processor:1.6.0")
    implementation("io.requery:requery-kotlin:1.6.0")
    implementation("mysql:mysql-connector-java:8.0.29")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.create<org.jmailen.gradle.kotlinter.tasks.LintTask>("ktLint") {
    group = "verification"
    source(files("src"))
}

tasks.create<org.jmailen.gradle.kotlinter.tasks.FormatTask>("ktFormat") {
    group = "formatting"
    source(files("src"))
}

tasks {
    shadowJar {
        dependsOn("kaptKotlin")
        dependsOn("ktLint")
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "dev.minjae.ticketbot.BootstrapKt"))
        }
    }
}


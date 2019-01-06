import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.10"
}

group = "me.limebyte"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-ui:1.1.0")
    implementation("com.illposed.osc:javaosc-core:0.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

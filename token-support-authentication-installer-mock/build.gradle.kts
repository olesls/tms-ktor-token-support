import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation(project(":token-support-azure-validation-mock"))
    implementation(project(":token-support-tokenx-validation-mock"))
    implementation(project(":token-support-idporten-sidecar-mock"))
    implementation(Ktor.serverAuth)
    testImplementation(kotlin("test-junit5"))
    testImplementation(Kluent.kluent)
    testImplementation(Ktor.serverTestHost)
    testImplementation(Mockk.mockk)
}

repositories {
    mavenCentral()
    mavenLocal()
}

publishing {
    repositories{
        mavenLocal()
    }

    publications {
        create<MavenPublication>("local") {
            from(components["java"])
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks {

    withType<Test> {
        useJUnitPlatform()
    }
}

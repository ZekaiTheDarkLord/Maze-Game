import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "org.example"
version = "BAD2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to "integration_tests/bad2/BadITest2Kt"))
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
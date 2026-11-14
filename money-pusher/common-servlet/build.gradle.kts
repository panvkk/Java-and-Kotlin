plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.kpanov.common"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }

dependencies {
    implementation(project(":common-core"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
}

tasks.bootJar { enabled = false }
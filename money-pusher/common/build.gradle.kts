plugins {
    id("java")
}

group = "com.kpanov.common"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.jsonwebtoken:jjwt-api:0.11.5") // заменить на api(), чтобы не дублировать зависимости
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
}
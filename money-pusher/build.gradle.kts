plugins {
    id("java")
    id("org.springframework.boot") version "4.1.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {  }

tasks.test {
    useJUnitPlatform()
}
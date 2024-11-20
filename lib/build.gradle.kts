plugins {
    id("java-library")
}

group = "com.github.traunin"
version = "1.0.0"

base {
    archivesName = rootProject.name
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.javadoc {
    title = "Library API Documentation"

    options {
        encoding = "UTF-8"
    }
}
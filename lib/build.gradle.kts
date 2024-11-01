plugins {
    id("java-library")
}

group = "com.github.traunin"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    title = "Library API Documentation"

    options {
        encoding = "UTF-8"
    }
}
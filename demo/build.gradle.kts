plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

group = "com.github.traunin"
version = "1.0.0"

repositories {
    mavenCentral()
    maven(url="../libs")
}
dependencies {
    implementation(project(":lib"))
    implementation("com.github.shimeoki:jfx-rasterization:1.0.0")
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass = "com.github.traunin.triangulation.demo.App"
}

tasks.named("javadoc") {
    enabled = false
}
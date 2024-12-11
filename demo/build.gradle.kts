plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

group = "io.github.traunin"
version = "1.0.1"

repositories {
    mavenCentral()
}
dependencies {
    implementation(project(":lib"))
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass = "io.github.traunin.triangulation.demo.App"
}

tasks.named("javadoc") {
    enabled = false
}
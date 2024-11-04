plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

group = "com.github.traunin"
version = "0.1.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation(project(":lib"))
    implementation(files("libs/lib-0.7.1.jar"))
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
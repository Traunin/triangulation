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
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass = "com.github.traunin.triangulation.demo.App"
}
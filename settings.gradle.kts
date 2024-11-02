rootProject.name = "triangulation"
include("lib")
include("demo")

plugins {
    // automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

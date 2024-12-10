plugins {
    id("java-library")
    id("maven-publish")
    signing
}

group = "io.github.traunin"
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

//tasks.javadoc {
//    title = "Library API Documentation"
//
//    options {
//        encoding = "UTF-8"
//    }
//}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])
            // TODO
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name = rootProject.name
                description = "A Java triangulation library"
                // TODO
                url = "https://github.com/Traunin/triangulation"
                // TODO
                properties = mapOf(
                    "myProp" to "value",
                    "prop.with.dots" to "anotherValue"
                )
                licenses {
                    license {
                        name = "MIT"
                        url = "https://github.com/Traunin/triangulation/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "Traunin"
                        name = "Denis"
                        email = "traunin5@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git:/github.com/triangulation.git"
                    developerConnection = "scm:git:ssh://github.com/triangulation.git"
                    url = "https://github.com/Traunin/triangulation"
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
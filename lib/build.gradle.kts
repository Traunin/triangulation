plugins {
    id("base")
    `kotlin-dsl`

    id("java-library")
    id("java-library-distribution")

    id("io.deepmedia.tools.deployer") version "0.15.0"
}

group = "io.github.traunin"
version = "1.1.1"

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
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withJavadocJar()
    withSourcesJar()
}

deployer {
    content {
        component {
            fromJava()
        }
    }

    projectInfo {
        name.set(rootProject.name)
        description.set("A Java triangulation library.")
        url.set("https://github.com/Traunin/triangulation")

        artifactId.set(rootProject.name)

        scm {
            fromGithub("Traunin", "triangulation")
        }

        license(MIT)

        developer("Traunin", "traunin5@gmail.com")
    }

    localSpec("m2") {
    }

    localSpec("artifact") {
        directory.set(file("build/artifact"))
    }

    centralPortalSpec {
        auth.user.set(secret("CENTRAL_PORTAL_USERNAME"))
        auth.password.set(secret("CENTRAL_PORTAL_PASSWORD"))

        signing {
            key.set(secret("GPG_KEY"))
            password.set(secret("GPG_PWD"))
        }
    }

    githubSpec {
        owner.set("Traunin")
        repository.set("triangulation")

        auth.user.set(secret("GITHUB_ACTOR"))
        auth.token.set(secret("GITHUB_TOKEN"))
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
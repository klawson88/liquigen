plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm").version("1.9.10")

    // Apply the Java Gradle plugin to add support for Gradle plugin development
    `java-gradle-plugin`

    // Apply the Signing plugin to add support for the cryptographic signing of the assets published by this plugin
    signing

    // Apply the Gradle Plugin Publishing plugin to add support for
    // the publication of this plugin to the Gradle Plugin Portal
    id("com.gradle.plugin-publish").version("1.2.1")

    // Apply the Gradle Credentials plugin to add support for password-based
    // encryption of the credentials to be used to sign and publish this plugin
    id("nu.studer.credentials").version("3.0")
}

kotlin {
    jvmToolchain(11)
}

group = "io.github.klawson88"
version = "1.2.4"

gradlePlugin {
    plugins {
        create("liquigen") {
            id = "io.github.klawson88.liquigen"
            implementationClass = "com._16minutes.liquigen.Liquigen"
            displayName = "Liquigen"
            description = """A plugin which provides the ability to generate Liquibase
                | changelogs with standardized name and content structure""".trimMargin()
            tags = listOf("liquibase")
            website = "https://github.com/klawson88/liquigen"
            vcsUrl = "https://github.com/klawson88/liquigen.git"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.7.1") {
        because("it (Kotest) is the framework with which the plugin will be tested")
    }

    testImplementation("io.kotest:kotest-assertions-core:5.7.1") {
        because("""it (Kotest assertions library) is the library that defines
            | the assertions to be used in tests of the plugin""".trimMargin())
    }

    testImplementation("io.mockk:mockk:1.13.7") {
        because("it (Mockk) is the mocking library to be used in the tests of the plugin")
    }
}

tasks.named("publishPlugins") {
    doFirst {
        val credentialsContainer =
            project.property("credentials") as nu.studer.gradle.credentials.domain.CredentialsContainer

        project.ext["gradle.publish.key"] = credentialsContainer.forKey("gradle.publish.key")
        project.ext["gradle.publish.secret"] = credentialsContainer.forKey("gradle.publish.secret")
    }
}

tasks.withType(Sign::class) {
    doFirst {
        val credentialsContainer =
            project.property("credentials") as nu.studer.gradle.credentials.domain.CredentialsContainer

        project.ext["signing.keyId"] = credentialsContainer.forKey("signing.keyId")
        project.ext["signing.password"] = credentialsContainer.forKey("signing.password")
        project.ext["signing.secretKeyRingFile"] = credentialsContainer.forKey("signing.secretKeyRingFile")
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform ()
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.geometry:s2-geometry:2.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

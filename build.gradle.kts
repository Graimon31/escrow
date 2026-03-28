plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.spring.boot.get().pluginId)
    apply(plugin = rootProject.libs.plugins.spring.dependency.management.get().pluginId)

    group = "com.escrow"
    version = "0.0.1-SNAPSHOT"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(rootProject.libs.spring.boot.starter.actuator)
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
        testImplementation(rootProject.libs.spring.boot.starter.test)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

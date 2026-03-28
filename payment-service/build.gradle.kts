dependencies {
    implementation(rootProject.libs.spring.boot.starter.web)
    implementation(rootProject.libs.spring.boot.starter.data.jpa)
    implementation(rootProject.libs.spring.boot.starter.validation)
    implementation(rootProject.libs.flyway.core)
    implementation(rootProject.libs.flyway.postgresql)
    implementation(rootProject.libs.postgresql)

    testImplementation(platform(rootProject.libs.testcontainers.bom))
    testImplementation(rootProject.libs.testcontainers.junit)
    testImplementation(rootProject.libs.testcontainers.postgresql)
    testImplementation(rootProject.libs.spring.boot.testcontainers)
}

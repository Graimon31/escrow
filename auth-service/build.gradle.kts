dependencies {
    implementation(rootProject.libs.spring.boot.starter.web)
    implementation(rootProject.libs.spring.boot.starter.data.jpa)
    implementation(rootProject.libs.spring.boot.starter.security)
    implementation(rootProject.libs.spring.boot.starter.validation)
    implementation(rootProject.libs.flyway.core)
    implementation(rootProject.libs.flyway.postgresql)
    implementation(rootProject.libs.postgresql)
    implementation(rootProject.libs.jjwt.api)
    runtimeOnly(rootProject.libs.jjwt.impl)
    runtimeOnly(rootProject.libs.jjwt.jackson)

    testImplementation(platform(rootProject.libs.testcontainers.bom))
    testImplementation(rootProject.libs.testcontainers.junit)
    testImplementation(rootProject.libs.testcontainers.postgresql)
    testImplementation(rootProject.libs.spring.boot.testcontainers)
    testImplementation(rootProject.libs.spring.boot.starter.security) {
        // spring-security-test is in spring-boot-starter-test
    }
}

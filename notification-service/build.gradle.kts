dependencies {
    implementation(rootProject.libs.spring.boot.starter.web)
    implementation(rootProject.libs.spring.boot.starter.data.jpa)
    implementation(rootProject.libs.spring.kafka)
    implementation(rootProject.libs.flyway.core)
    implementation(rootProject.libs.flyway.postgresql)
    runtimeOnly(rootProject.libs.postgresql)
}

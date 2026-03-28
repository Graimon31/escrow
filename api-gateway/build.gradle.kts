dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${rootProject.libs.versions.spring.cloud.get()}")
    }
}

dependencies {
    implementation(rootProject.libs.spring.cloud.starter.gateway)
    implementation(rootProject.libs.jjwt.api)
    runtimeOnly(rootProject.libs.jjwt.impl)
    runtimeOnly(rootProject.libs.jjwt.jackson)
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${rootProject.libs.versions.spring.cloud.get()}")
    }
}

dependencies {
    implementation(rootProject.libs.spring.cloud.starter.gateway)
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
    }
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootWar

plugins {
    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
    war
}

group = "io.playce"
version = "1.0.2"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("commons-codec:commons-codec:1.15")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("dev.akkinoc.util:yaml-resource-bundle:2.7.2")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootWar> {
    enabled = true
    archiveFileName.set("playce-oauth.war")
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
    }
}
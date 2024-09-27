import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation("io.quarkiverse.quinoa:quarkus-quinoa:2.3.6")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-security-jpa-reactive")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-hibernate-reactive-panache-kotlin")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    testImplementation("io.quarkus:quarkus-test-security")
    testImplementation("io.quarkus:quarkus-jacoco")
    testImplementation("io.quarkus:quarkus-junit5:3.9.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.3.2")
    testImplementation("io.quarkus:quarkus-test-vertx")
}

group = "io.tohuwabohu"

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
allOpen {
    annotation("javax.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.IdClass")
    annotation("jakarta.persistence.MappedSuperclass")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        javaParameters = true
    }
}

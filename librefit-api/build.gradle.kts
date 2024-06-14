plugins {
    kotlin("jvm") version "2.0.0"
    id("org.openapi.generator") version "6.3.0"
    id("com.github.node-gradle.node") version "3.5.1"
    kotlin("plugin.serialization") version "2.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
}

tasks.openApiValidate {
    inputSpec.set("${project.projectDir}/openapi.yaml")
}

tasks.register<JavaExec>("ApiCodegen") {
    dependsOn(tasks.openApiValidate)

    classpath = sourceSets.test.get().runtimeClasspath

    mainClass.set("io.tohuwabohu.librefit.api.codegen.ApiCodegen")
    args = listOf("${project.projectDir}/openapi.json", "${project.projectDir}/rest/lib")

    doFirst {
        project.mkdir("${project.projectDir}/rest/lib/api")
    }

    doLast {
        copy{
            from("${project.projectDir}/rest/lib")
            into("../librefit-web/src/lib")
        }
    }
}

tasks.build {
    dependsOn("ApiCodegen")
}
plugins {
    kotlin("jvm")
    id("org.openapi.generator") version "7.7.0"
    kotlin("plugin.serialization")
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
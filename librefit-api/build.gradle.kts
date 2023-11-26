plugins {
    kotlin("jvm") version "1.7.22"
    id("org.openapi.generator") version "6.3.0"
    id("com.github.node-gradle.node") version "3.5.1"
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.openApiValidate {
    inputSpec.set("${project.projectDir}/openapi.yaml")
}

tasks.register<JavaExec>("ApiCodegen") {
    dependsOn(tasks.openApiValidate)

    classpath = sourceSets.test.get().runtimeClasspath

    main = "io.tohuwabohu.librefit.api.codegen.ApiCodegen"
    args = listOf("${project.projectDir}/openapi.json", "${project.projectDir}/rest/lib")

    doFirst {
        project.mkdir("${project.projectDir}/rest/lib/api")
        project.mkdir("${project.projectDir}/rest/lib/server/api")
    }

    doLast {
        copy{
            from("${project.projectDir}/rest/lib")
            into("../librefit-web/src/lib")
        }
    }
}


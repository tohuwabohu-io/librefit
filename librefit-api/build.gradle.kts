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

node {
    version.set("19.6.0")
    npmVersion.set("9.4.0")
    npmInstallCommand.set("install")
    npmWorkDir.set(file("${project.projectDir}/.cache/npm"))
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
}

tasks.openApiValidate {
    inputSpec.set("openapi.yaml")
}

tasks.register<JavaExec>("ApiCodegen") {
    dependsOn(tasks.openApiValidate)

    classpath = sourceSets.test.get().runtimeClasspath
    main = "io.tohuwabohu.librefit.api.codegen.ApiCodegen"
    args = listOf("${project.projectDir}/openapi.json", "${project.projectDir}/rest/index.js")

    doLast {
        copy{
            from("${project.projectDir}/rest/index.js")
            into("../librefit-web/src/lib/api/")
        }
    }
}


plugins {
    kotlin("jvm") version "1.7.22"
    id("org.openapi.generator") version "6.3.0"
    id("com.github.node-gradle.node") version "3.5.1"
}

repositories {
    mavenCentral()
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

tasks.openApiGenerate {
    dependsOn(tasks.openApiValidate)

    inputSpec.set("openapi.yaml")
    outputDir.set("${project.projectDir}/rest")
    generatorName.set("typescript-axios")
    additionalProperties.put("supportsES6", "true")
}

tasks.named<com.github.gradle.node.npm.task.NpmTask>("npm_run_link") {
    dependsOn(tasks.npmInstall)
}

tasks.named<com.github.gradle.node.npm.task.NpmTask>("npm_run_build") {
    dependsOn(tasks.npmInstall)
    dependsOn(tasks.openApiGenerate)
}

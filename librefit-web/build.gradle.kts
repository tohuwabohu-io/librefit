plugins {
    kotlin("jvm") version "1.7.22"
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

tasks.npmInstall {
    nodeModulesOutputFilter {
        exclude("nonExistingFile")
    }
}

tasks.named<com.github.gradle.node.npm.task.NpmTask>("npm_run_build") {
    dependsOn(tasks.npmInstall)

    inputs.dir(fileTree("src"))
    inputs.dir(fileTree("static"))

    inputs.file("package.json")
    inputs.file("package-lock.json")

    outputs.dir("build")
}

tasks.named<com.github.gradle.node.npm.task.NpmTask>("npm_run_dev") {
    dependsOn(tasks.npmInstall)
}
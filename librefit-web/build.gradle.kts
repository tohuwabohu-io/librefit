plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.node-gradle.node") version "3.5.1"
}

repositories {
    mavenCentral()
}

node {
    version.set("21.7.0")
    npmVersion.set("10.5.0")
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

tasks.build {
    dependsOn("npm_ci")
    dependsOn("npm_run_build")
}
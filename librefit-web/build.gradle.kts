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
    inputSpec.set("../librefit-api/openapi.yaml")
}

tasks.openApiGenerate {
    dependsOn(tasks.openApiValidate)

    inputSpec.set("../librefit-api/openapi.yaml")
    outputDir.set("${project.projectDir}/src/lib/gen/api")
    generatorName.set("typescript-axios")
    additionalProperties.put("supportsES6", "true")

    doLast {
        delete("${project.projectDir}/src/lib/gen/api/.openapi-generator")
        delete("${project.projectDir}/src/lib/gen/api/.gitignore")
        delete(fileTree("${project.projectDir}/src/lib/gen/api").matching {
            exclude("*.ts")
        })
    }
}

tasks.npmInstall {
    nodeModulesOutputFilter {
        exclude("nonExistingFile")
    }
}

tasks.named<com.github.gradle.node.npm.task.NpmTask>("npm_run_build") {
    dependsOn(tasks.npmInstall)
}

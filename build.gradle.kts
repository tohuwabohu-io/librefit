plugins {
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
    kotlin("plugin.allopen") apply false
    kotlin("plugin.noarg") apply false
    kotlin("plugin.jpa") apply false

    id("io.quarkus") apply false
}

project(":librefit-api") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
}

project(":librefit-service") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    apply(plugin = "io.quarkus")
}

project(":librefit-web") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
}
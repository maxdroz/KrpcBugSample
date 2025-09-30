plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kotlinx.rpc.plugin)
}

application {
    mainClass = "com.example.client.ClientMainKt"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinx.rpc.krpc.ktor.client)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
}
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bcrypt)
    implementation(libs.auth0.jwt)

    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

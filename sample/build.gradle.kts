plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinx.coroutines.core)
}

tasks.register<JavaExec>("runSample") {
    group = "application"
    description = "Chama o core com a public key definida em MP_PUBLIC_KEY"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.mercadopago.sdk.core.sample.MainKt")
}

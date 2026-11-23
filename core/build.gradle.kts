import java.security.MessageDigest
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    `maven-publish`
}

// Gera a constante CORE_VERSION com a versão do projeto, enviada no header SDK-version.
val generateCoreVersion by tasks.registering {
    val coreVersion = project.version.toString()
    val outputDirectory = layout.buildDirectory.dir("generated/coreVersion/commonMain/kotlin")
    inputs.property("coreVersion", coreVersion)
    outputs.dir(outputDirectory)
    doLast {
        val escapedCoreVersion = coreVersion
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("$", "\\$")
        val file = outputDirectory.get().file("com/mercadopago/sdk/core/CoreVersion.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            |package com.mercadopago.sdk.core
            |
            |internal const val CORE_VERSION: String = "$escapedCoreVersion"
            |
            """.trimMargin(),
        )
    }
}

kotlin {
    explicitApi()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }
    jvm()

    // O XCFramework exporta o :core-common (MPResult, MPResultError) para os headers do Swift.
    val xcframework = XCFramework("MercadoPagoCore")
    iosArm64 {
        binaries.framework {
            baseName = "MercadoPagoCore"
            export(project(":core-common"))
            xcframework.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "MercadoPagoCore"
            export(project(":core-common"))
            xcframework.add(this)
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("com.mercadopago.sdk.common.InternalMercadoPagoApi")
        }
        commonMain {
            kotlin.srcDir(generateCoreVersion)
        }
        commonMain.dependencies {
            api(project(":core-common"))
            implementation(project(":network"))
            // JsonObject do device no corpo de card_tokens.
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.mercadopago.sdk.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// O teste JVM confere que CORE_VERSION é a versão do build.
tasks.named<Test>("jvmTest") {
    systemProperty("mercadopago.core.version", project.version.toString())
}

// XCFramework compactado para release no iOS.
val zipXCFramework by tasks.registering(Zip::class) {
    dependsOn("assembleMercadoPagoCoreReleaseXCFramework")
    from(layout.buildDirectory.dir("XCFrameworks/release"))
    archiveFileName.set("MercadoPagoCore-${project.version}.xcframework.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
}

// Checksum SHA-256 para o binaryTarget do Package.swift.
val xcframeworkChecksum by tasks.registering {
    dependsOn(zipXCFramework)
    doLast {
        val file = zipXCFramework.get().archiveFile.get().asFile
        val hex = MessageDigest.getInstance("SHA-256")
            .digest(file.readBytes())
            .joinToString("") { "%02x".format(it) }
        file.resolveSibling("${file.name}.sha256").writeText(hex)
        println("SHA-256 de ${file.name}: $hex")
    }
}

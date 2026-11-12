import java.security.MessageDigest
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    `maven-publish`
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
        commonMain.dependencies {
            api(project(":core-common"))
            implementation(project(":network"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.json)
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

import org.gradle.api.publish.PublishingExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
}

subprojects {
    group = "com.mercadopago.sdk"
    version = providers.gradleProperty("version").getOrElse("0.0.0-SNAPSHOT")

    // Repositório de publicação: URL e credenciais vêm de propriedades do Gradle (CI), nunca do código.
    pluginManager.withPlugin("maven-publish") {
        extensions.configure<PublishingExtension> {
            repositories {
                val publishUrl = providers.gradleProperty("publishUrl").orNull
                if (publishUrl != null) {
                    maven {
                        name = "release"
                        url = uri(publishUrl)
                        credentials {
                            username = providers.gradleProperty("publishUser").orNull
                            password = providers.gradleProperty("publishPassword").orNull
                        }
                    }
                }
            }
        }
    }
}

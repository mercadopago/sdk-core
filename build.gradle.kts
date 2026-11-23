import org.gradle.api.publish.PublishingExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
}

val projectVersion = providers.gradleProperty("version").getOrElse("0.0.0-SNAPSHOT")

subprojects {
    group = "com.mercadopago.sdk"
    version = projectVersion

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

// Publicar no repositório remoto sem publishUrl deve parar com mensagem clara, em vez de não fazer nada.
val remotePublishUrlPresent = !providers.gradleProperty("publishUrl").orNull.isNullOrBlank()
gradle.taskGraph.whenReady {
    val publishesRemotely = allTasks.any { it.name == "publish" || it.name.contains("PublicationToReleaseRepository") }
    if (publishesRemotely && !remotePublishUrlPresent) {
        throw GradleException("publishUrl não definida. Passe -PpublishUrl=<url>, -PpublishUser e -PpublishPassword pelo CI. Use publishToMavenLocal para testes locais.")
    }
    // A versão SNAPSHOT iria no header SDK-version do tráfego real. Publicar exige -Pversion do CI.
    if (publishesRemotely && projectVersion.endsWith("-SNAPSHOT")) {
        throw GradleException("Versão SNAPSHOT não pode ser publicada no repositório remoto. Passe -Pversion=<versão> pelo CI. Use publishToMavenLocal para testes locais.")
    }
}

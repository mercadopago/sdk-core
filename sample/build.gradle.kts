plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinx.coroutines.core)
}

tasks.register<JavaExec>("runSample") {
    group = "application"
    description = "Chama o core. Obrigatórias: MP_PUBLIC_KEY, MP_PRODUCT_ID e MP_BIN. Opcionais: MP_PAYMENT_METHOD_ID (padrão: primeiro método do BIN), MP_AMOUNT (padrão: 100.0), MP_SAVED_CARD_ID e MP_SAVED_CARD_SECURITY_CODE (sem os dois, pula o token do cartão salvo)"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.mercadopago.sdk.core.sample.MainKt")
}

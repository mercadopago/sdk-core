# fury_openplatform-sdk-core

`MercadoPagoCore`: serviços e regras de negócio dos SDKs do Mercado Pago, em Kotlin Multiplatform. Os SDKs Android e iOS ficam só com o frontend e consomem este core.

- Pacote: `com.mercadopago.sdk.core`.
- iOS: XCFramework `MercadoPagoCore`.

## Módulos

| Módulo | Pacote | Conteúdo |
|---|---|---|
| `:core-common` | `com.mercadopago.sdk.common` | `MPResult` e `MPResultError`, devolvidos ao integrador |
| `:network` | `com.mercadopago.sdk.network` | Estrutura genérica de consumo de API, sem endpoints. Uso interno dos módulos |
| `:core` | `com.mercadopago.sdk.core` | Fachada `MercadoPagoCore` e serviços por domínio |

O integrador enxerga o `:core-common` e o `:core`. O `:network` só entra em tempo de execução no Android e não é exportado no XCFramework.

## Estado

Esqueleto de fundação (spec S03). Ainda não há API pública: só o `expect`/`actual` de plataforma, para validar o pipeline de build nas plataformas.

## Como rodar

Requisitos: JDK 17 e Android SDK (variável `ANDROID_HOME` ou `local.properties` com `sdk.dir`, que não vai para o git).

```bash
./gradlew :core:jvmTest      # testes na JVM
./gradlew :core:assemble     # compila Android e iOS
```

## Estrutura

- `core/src/commonMain`: código comum.
- `core/src/androidMain`, `jvmMain`, `iosMain`: implementações de plataforma.
- `core/src/commonTest`: testes que rodam em todas as plataformas de teste.

## Publicação

Grupo `com.mercadopago.sdk` (a confirmar no G0). Artefatos gerados: `core-android` e `core-jvm`. A versão vem de `-Pversion=...`, e o padrão é `0.0.0-SNAPSHOT`.

```bash
./gradlew :core:publish -Pversion=1.0.0 -PpublishUrl=<url> -PpublishUser=<user> -PpublishPassword=<token>
./gradlew :core:zipXCFramework :core:xcframeworkChecksum   # iOS: zip e SHA-256
```

URL e credenciais chegam por propriedades do Gradle, no CI. Nunca entram no código. Sem `publishUrl`, a tarefa `publish` não tem repositório de destino.

Canal público, visibilidade e grupo final dependem das decisões da S03 (AppSec e plataforma).

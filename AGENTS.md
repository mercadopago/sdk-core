# Regras para desenvolvimento com IA

Este arquivo vale para qualquer agente de código (Claude Code, Codex e outros) e para quem revisa o código gerado. O `CLAUDE.md` importa este arquivo.

> Este repositório é espelhado publicamente. Não escreva aqui, nem em nenhum outro arquivo, URLs internas, nomes de canais internos, credenciais ou detalhes de infraestrutura.

## O que é este projeto

Core em Kotlin Multiplatform compartilhado pelos SDKs do Mercado Pago. Targets: Android, JVM, iOS (`iosArm64` e `iosSimulatorArm64`).

## Módulos

| Módulo | Conteúdo | Como os outros módulos dependem dele |
|---|---|---|
| `:core-common` | Tipos públicos compartilhados e devolvidos ao integrador (`MPResult`, `MPResultError`) e a anotação `@InternalMercadoPagoApi` | `api`, e exportado no XCFramework |
| `:network` | Estrutura genérica de consumo de API (`ApiClient`, `ApiRequest`, `createApiClient`). Não conhece nenhum endpoint | `implementation`, e **nunca** exportado no XCFramework |
| `:core` | Fachada `MercadoPagoCore` e os serviços por domínio, com os seus endpoints | Gera o XCFramework `MercadoPagoCore` |

- **Endpoints ficam no módulo da feature**, declarados como `ApiRequest`, e nunca no `:network`.
- **A API do `:network` é `public` só porque outros módulos a usam.** Toda declaração pública dele leva `@InternalMercadoPagoApi`, e quem a usa declara o opt-in no `build.gradle.kts`. O `:network` não entra na API do integrador: no Android, porque os módulos dependem dele com `implementation`; no iOS, porque não é exportado.
- **O Ktor fica só no `:network`.** Os outros módulos dependem da interface `ApiClient`, e os testes deles usam um `ApiClient` falso.

## Arquitetura

- **O core concentra os serviços e a lógica de negócio:** chamadas de API, tradução de erro, validações e regras, analytics, device session id e resolução do site.
- **Os SDKs (Android, iOS e futuros, como React Native e Flutter) ficam só com o frontend:** telas, componentes e campos seguros, que chamam o core.
- **Integração só pelo Builder** da fachada pública. Não crie construtores públicos alternativos.
- **Camadas (arquitetura limpa):** fachada pública → caso de uso → repositório → data source.
  - `domain`: modelos, casos de uso e as **interfaces** dos repositórios. Não depende de `data`, de rede nem de plataforma.
  - `data`: implementações dos repositórios, data sources (um por API), DTOs e mappers.
  - A dependência sempre aponta para o domínio. A composição fica numa função de criação, sem framework de DI.
- **Código comum em `commonMain`.** Use `expect`/`actual` só para o que depende de plataforma (engine HTTP, tradução de exceções de transporte).
- **Visibilidade padrão é `internal` ou `private`.** Só é `public` o que o SDK usa na integração: a fachada, o Builder, os modelos devolvidos ao integrador e os tipos de resultado e erro. Repositórios, casos de uso, data sources, DTOs, mappers e o cliente HTTP nunca são públicos. Na dúvida, use `internal`.
- **A API pública não expõe tipos do Ktor** nem de outras bibliotecas.
- O build do módulo ativa o [modo de API explícita](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors) do Kotlin (`kotlin { explicitApi() }`), que exige declarar a visibilidade e o tipo de retorno de toda declaração pública. Uma declaração sem modificador não compila.
- **Resultado das operações:** `Result<Success, ResultError>`. Não lance exceções para erros esperados de API ou de rede.

## Referências obrigatórias

Todo código segue, nesta ordem de prioridade, as regras deste arquivo e depois:

- **Guias do Google para Android:**
  - [Guia de arquitetura de apps](https://developer.android.com/topic/architecture) e as [recomendações de arquitetura](https://developer.android.com/topic/architecture/recommendations): camadas de dados e de domínio, repositórios como fonte única e casos de uso com uma única responsabilidade.
  - [Guia de estilo Kotlin do Android](https://developer.android.com/kotlin/style-guide).
- **Kotlin Multiplatform:** [convenções de código do Kotlin](https://kotlinlang.org/docs/coding-conventions.html) e a [estrutura de projetos KMP](https://kotlinlang.org/docs/multiplatform-discover-project.html).
- **Arquitetura limpa e SOLID:** regra de dependência apontando para o domínio, inversão de dependência por interfaces, classes pequenas com uma responsabilidade.
- **Padrões de projeto:** use o padrão conhecido em vez de uma solução própria. Os já adotados aqui são Builder (integração), Repository (dados), Adapter (`expect`/`actual` por plataforma) e Factory (criação do cliente HTTP e da composição). Ao introduzir um padrão novo, explique o motivo na descrição do PR.

Quando uma dessas referências conflitar com as regras de segurança acima, a segurança prevalece.

## Segurança

- Nunca registre em log request, response, URL completa ou `public_key`. Não instale plugin de log no cliente HTTP.
- Mensagens de erro nunca incluem o texto de exceções de transporte, que podem conter a URL com a `public_key`.
- A base URL só é aceita com HTTPS e host na allowlist. Redirecionamentos ficam desligados.
- Dados de cartão (`card_tokens`) são PCI: nunca em log, nunca persistidos, nunca em `toString()` de modelos.
- Nenhum segredo, token ou credencial no código. Publicação usa propriedades do Gradle fornecidas pelo CI.
- Só plugins e dependências de repositórios públicos.

## Dependências

- Toda dependência e todo plugin são declarados no catálogo `gradle/libs.versions.toml`, no padrão do SDK Android: versões em `[versions]` (camelCase), bibliotecas em `[libraries]` e plugins em `[plugins]` (kebab-case, com `version.ref`).
- Os `build.gradle.kts` usam só `libs.*` e `alias(libs.plugins.*)`. Não declare coordenadas nem versões direto no build.

## Testes

- Todo código novo tem teste em `commonTest`. Testes específicos de plataforma ficam em `jvmTest`, `androidUnitTest` ou `iosTest`.
- Nome no formato `given ... when ... then ...`, com os comentários `// Given`, `// When` e `// Then` no corpo.
- HTTP é testado com `MockEngine`, cobrindo sucesso, erro com JSON, erro sem JSON, corpo vazio e falha de transporte.
- Fixtures de API são anonimizadas.

## Estilo

- Convenções oficiais de Kotlin (`kotlin.code.style=official`).
- **Classes, interfaces, funções e propriedades `internal` ou `private` não têm documentação.** Não adicione KDoc (`/** ... */`) nelas, nem ao criar nem ao editar. Se o código precisa de explicação, prefira nomes mais claros.
- Documentação (KDoc) só na API pública, que é o que o integrador vê.
- **Arquivos pequenos, uma responsabilidade por arquivo.** Uma classe, interface ou objeto por arquivo, com o nome do arquivo igual ao da declaração. Cada papel tem o seu arquivo: por exemplo, o DTO fica em `IdentificationTypeDto.kt` e o mapper em `IdentificationTypeMapper.kt`, nunca juntos. Só funções `private` usadas pela própria declaração ficam no mesmo arquivo. Não crie arquivos genéricos como `Utils.kt` ou `Extensions.kt` que acumulam assuntos diferentes.
- Nomes de código e mensagens de commit em inglês ou português, seguindo o histórico do repositório.

## Como validar

```bash
./gradlew :core:jvmTest :core:testDebugUnitTest :core:iosSimulatorArm64Test
```

O target iOS precisa do Xcode completo instalado.

## Pull requests

- PRs pequenos, um assunto por PR.
- Descrição com contexto, o que muda, como testar e o que ficou de fora.
- Diferenças de comportamento em relação ao SDK legado devem estar explícitas na descrição.

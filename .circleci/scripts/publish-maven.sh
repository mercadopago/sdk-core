#!/usr/bin/env bash
set -euo pipefail

# Publica core-common, network e core no canal Maven. Roda só em tag (ver config.yml).
# Em tag, versão já publicada é erro, não pulo: o canal não aceita sobrescrita.

: "${VERSION:?VERSION não definida (vem da tag)}"
: "${PUBLISH_URL:?PUBLISH_URL não definida (secret do contexto)}"
: "${PUBLISH_USER:?PUBLISH_USER não definida (secret do contexto)}"
: "${PUBLISH_PASSWORD:?PUBLISH_PASSWORD não definida (secret do contexto)}"

# O pom do módulo core é o ponto de checagem: se existe, a versão já foi publicada.
POM_PATH="com/mercadopago/sdk/core/${VERSION}/core-${VERSION}.pom"
STATUS=$(curl -s -o /dev/null -w '%{http_code}' \
  --config <(printf 'user = "%s:%s"\n' "$PUBLISH_USER" "$PUBLISH_PASSWORD") \
  "${PUBLISH_URL%/}/${POM_PATH}")

if [[ "$STATUS" == "200" ]]; then
  echo "A versão ${VERSION} já existe no canal. Crie uma tag nova."
  exit 1
fi

./gradlew :core-common:publish :network:publish :core:publish \
  -Pversion="$VERSION" \
  -PpublishUrl="$PUBLISH_URL" \
  -PpublishUser="$PUBLISH_USER" \
  -PpublishPassword="$PUBLISH_PASSWORD"

echo "Publicado: ${VERSION}"

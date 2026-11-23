#!/usr/bin/env bash
set -euo pipefail

# Publica core-common, network e core no canal Maven. Roda só em tag (ver config.yml).
# Em tag, versão já publicada é erro, não pulo: o canal não aceita sobrescrita.
# Publicação parcial (alguns módulos já existem) também é erro: não sobrescrevemos, a limpeza é manual.

: "${VERSION:?VERSION não definida (vem da tag)}"
: "${PUBLISH_URL:?PUBLISH_URL não definida (secret do contexto)}"
: "${PUBLISH_USER:?PUBLISH_USER não definida (secret do contexto)}"
: "${PUBLISH_PASSWORD:?PUBLISH_PASSWORD não definida (secret do contexto)}"

MODULES=(core-common network core)
published=""
published_count=0
missing=""

# O pom de cada módulo é o ponto de checagem: se existe, o módulo já foi publicado nesta versão.
for module in "${MODULES[@]}"; do
  pom_path="com/mercadopago/sdk/${module}/${VERSION}/${module}-${VERSION}.pom"
  status=$(curl -s -o /dev/null -w '%{http_code}' \
    --config <(printf 'user = "%s:%s"\n' "$PUBLISH_USER" "$PUBLISH_PASSWORD") \
    "${PUBLISH_URL%/}/${pom_path}")

  case "$status" in
    200)
      published="${published} ${module}"
      published_count=$((published_count + 1))
      ;;
    404)
      missing="${missing} ${module}"
      ;;
    *)
      echo "Falha ao verificar ${module} ${VERSION} no canal (HTTP ${status}). Nada foi publicado."
      exit 1
      ;;
  esac
done

if [[ "$published_count" -eq "${#MODULES[@]}" ]]; then
  echo "A versão ${VERSION} já existe no canal. Crie uma tag nova."
  exit 1
fi

if [[ "$published_count" -gt 0 ]]; then
  echo "Publicação parcial da versão ${VERSION}: já existem:${published}; faltam:${missing}."
  echo "O canal não aceita sobrescrita. Peça à plataforma a limpeza da versão e publique de novo com uma tag nova."
  exit 1
fi

# Credenciais vão por variáveis de ambiente (ORG_GRADLE_PROJECT_*), não por argumentos, para não aparecerem na lista de processos.
ORG_GRADLE_PROJECT_publishUser="$PUBLISH_USER" \
ORG_GRADLE_PROJECT_publishPassword="$PUBLISH_PASSWORD" \
./gradlew :core-common:publish :network:publish :core:publish \
  -Pversion="$VERSION" \
  -PpublishUrl="$PUBLISH_URL"

echo "Publicado: ${VERSION}"

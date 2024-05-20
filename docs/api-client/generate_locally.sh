#!/bin/bash

set -e

repo_root="../../"

api_client_root="$repo_root/build/api-client/"
[ -d "$api_client_root" ] && rm -rf "$api_client_root"
mkdir -p "$api_client_root"

# shellcheck disable=SC2016 # $VERSION is a placeholder
sed 's/$VERSION/0.0.0/g' package-template.json > "$api_client_root/package.json"

generator_cli_jar="openapi-generator-cli.jar"
# download if not present
[ ! -f "$generator_cli_jar" ] && wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/7.2.0/openapi-generator-cli-7.2.0.jar -O "$generator_cli_jar"

(cd "$repo_root" && ./gradlew build -x check)
java -jar "$generator_cli_jar" generate -i "$repo_root/build/openapi/openapi.yaml" -g typescript-fetch -o "$api_client_root" --additional-properties=typescriptThreePlus=true

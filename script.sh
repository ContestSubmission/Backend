DOCKER_METADATA_OUTPUT_LABELS="com.github.actions.run.id=6936546308
com.github.actions.run.number=14
org.opencontainers.image.created=2023-11-20T22:27:14.841Z
org.opencontainers.image.description=das hintere ende
org.opencontainers.image.licenses=MIT
org.opencontainers.image.revision=12480649355ee706484a57edacfab57186daf9a1
org.opencontainers.image.source=https://github.com/ContestSubmission/Backend
org.opencontainers.image.title=Backend
org.opencontainers.image.url=https://github.com/ContestSubmission/Backend
org.opencontainers.image.version=add-ci"

labels=$(echo "$DOCKER_METADATA_OUTPUT_LABELS" | awk -F '=' '{print "-Dquarkus.container-image.labels.\"" $1 "\"=" $2 ""}')

SAVEIFS=$IFS   # Save current IFS (Internal Field Separator)
IFS=$'\n'      # Change IFS to newline char
labels=($labels) # split the `names` string into an array by the same name
IFS=$SAVEIFS   # Restore original IFS

printf '%s\n' "${labels[@]}"
./gradlew \
	-Dquarkus.container-image.group="contestsubmission" \
	-Dquarkus.container-image.name="contestsubmission-backend" \
	-Dquarkus.container-image.tag='${quarkus.application.version}-${{ github.run_number }}' \
	-Dquarkus.container-image.additional-tags="$additional_tags" \
	"${labels[@]}" \
	clean quarkusDev

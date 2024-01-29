#!/bin/bash

if [[ "$#" -lt 1 ]]; then
	echo "Please provide a file name!"
	exit 1
fi

filename="$1"
filepath="$2"
if [[ -z "$filepath" ]]; then
	filepath="$filename"
fi

params="$(jq -r '.conditions | to_entries | map(select(.key != "key")) | map("-F \(.key)=\(.value|tostring)") | .[]' response.json)"

policy="$(jq -r '.conditions.policy' response.json | base64 -d)"
key="$(echo "$policy" | jq -r '.conditions[] | select(.[1] == "$key") | .[2]')"

upload_url="$(jq -r '.url' response.json)"
download_url="$upload_url/$key"
token="$(jq -r '.jwt' response.json)"

curl -F key="$key" $params -X POST "$upload_url" -F "file=@$filepath" -vLk --fail-with-body \
	&& printf "Successfully uploaded to %s\nToken: %s" "$download_url" "$token"

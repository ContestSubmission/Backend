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
key_prefix="$(echo "$policy" | jq -r '.conditions[] | select(.[1] == "$key") | .[2]')"

key="$key_prefix$filename"
url="$(jq -r '.url' response.json)"

curl -F key="$key" $params -X POST "$url" -F "file=@$filepath" -v && echo "Successfully uploaded to $url/$key"

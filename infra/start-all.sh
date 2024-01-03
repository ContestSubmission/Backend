#!/bin/bash

for directory in */; do (cd "$directory" && docker compose up -d); done

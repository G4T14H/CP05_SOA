#!/bin/sh
set -eu
cd "$(dirname "$0")"
if [ -f release/autoescola-api.jar ]; then
 java -jar release/autoescola-api.jar
else
 if [ ! -f target/autoescola-api-1.0.0.jar ]; then sh ./mvnw clean verify; fi
 java -jar target/autoescola-api-1.0.0.jar
fi

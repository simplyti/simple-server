#!/usr/bin/env bash

VERSION=$(echo "$TRAVIS_TAG" | sed 's/^v//g')
echo "Release version $VERSION"
mvn versions:set -DnewVersion=$VERSION -DgenerateBackupPoms=false

openssl aes-256-cbc -K $encrypted_25a884814f46_key -iv $encrypted_25a884814f46_iv -in ./.travis/codesigning.asc.enc -out codesigning.asc -d
gpg --fast-import codesigning.asc

mvn --settings=./.travis/settings.xml -pl !acceptance compile jar:jar source:jar-no-fork javadoc:jar gpg:sign nexus-staging:deploy

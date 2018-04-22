#!/usr/bin/env bash

openssl aes-256-cbc -K $encrypted_1ed45ccca01f_key -iv $encrypted_1ed45ccca01f_iv -in ./.travis/codesigning.asc.enc -out codesigning.asc -d
gpg --fast-import codesigning.asc

mvn --settings=./.travis/settings.xml compile jar:jar source:jar-no-fork javadoc:jar gpg:sign deploy:deploy
#!/usr/bin/env bash
#export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
#gpg --keyserver http://keyserver.ubuntu.com:11371 --send-keys key
mvn install -DperformRelease=true -DcreateChecksum=true -Dgpg.passphrase=password
#mvn deploy -DperformRelease=true -DcreateChecksum=true -Dgpg.passphrase=password
#to install locally for tests, simple run mvn install
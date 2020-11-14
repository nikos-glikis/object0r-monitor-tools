#!/usr/bin/env bash
#export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
mvn install -DperformRelease=true -DcreateChecksum=true -Dgpg.passphrase=password
#mvn deploy -DperformRelease=true -DcreateChecksum=true -Dgpg.passphrase=password
#to install locally for tests, simple run mvn install